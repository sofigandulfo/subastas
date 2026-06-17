package com.tallerwebi.dominio.subasta;

import com.tallerwebi.dominio.excepcion.SubastaConOfertasException;
import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import com.tallerwebi.dominio.excepcion.SubastaNoEditableException;
import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.oferta.RepositorioOferta;
import com.tallerwebi.dominio.usuario.Usuario;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ServicioSubastaImpl implements ServicioSubasta {

  private static final long TAMANIO_MAXIMO_IMAGEN = 5 * 1024 * 1024; // 5MB

  private RepositorioSubasta repositorioSubasta;
  private RepositorioOferta repositorioOferta;

  @Autowired
  public ServicioSubastaImpl(
    RepositorioSubasta repositorioSubasta,
    RepositorioOferta repositorioOferta
  ) {
    this.repositorioSubasta = repositorioSubasta;
    this.repositorioOferta = repositorioOferta;
  }

  @Override
  public Subasta crearSubasta(Subasta subasta, MultipartFile imagen, Usuario creador)
    throws SubastaInvalidaExeption {
    validarSubasta(subasta);

    if (imagen != null && !imagen.isEmpty()) {
      if (imagen.getSize() > TAMANIO_MAXIMO_IMAGEN) {
        throw new SubastaInvalidaExeption();
      }

      try {
        subasta.getDetalle().setImagen(imagen.getBytes());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    subasta.setPrecioActual(subasta.getPrecioInicial());
    subasta.setCreador(creador);
    return repositorioSubasta.guardarSubasta(subasta);
  }

  @Override
  public void verificarPrecioMaximo(Long subastaId) {
    Subasta subasta = repositorioSubasta.obtenerSubasta(subastaId);
    if (subasta.getEstadoSubasta() == EstadoSubasta.CERRADA) {
      return; //si la subasta ya esta cerrada, no debería hacer nada
    }
    if (subasta.getPrecioActual() >= subasta.getPrecioMaximo()) {
      subasta.setEstadoSubasta(EstadoSubasta.CUENTA_ATRAS);
      // para probarlo podemos usar .plusMinutes en vez de .plusHours
      subasta.setFechaCierre(LocalDateTime.now().plusHours(2));
      repositorioSubasta.guardarSubasta(subasta);
    }
  }

  @Override
  public void cerrarSubastasPorTiempo() {
    List<Subasta> subastas = repositorioSubasta.obtenerSubastasPorVencer();
    for (Subasta subasta : subastas) {
      if (subasta.getFechaCierre().isBefore(LocalDateTime.now())) {
        List<Oferta> todasLasOfertas = repositorioOferta.obtenerMejoresOfertasPorSubasta(
          subasta.getId()
        );
        List<Usuario> podio = determinarPodio(todasLasOfertas);
        subasta.setPodio(podio);
        subasta.setEstadoSubasta(EstadoSubasta.CERRADA);
        repositorioSubasta.guardarSubasta(subasta);
      }
    }
  }

  // El servicio es el responsable de determinar quiénes ganaron, no el repositorio.
  // Recibe todas las ofertas ordenadas por monto DESC y devuelve los 3 usuarios
  // con la mayor oferta, sin repetir usuario.
  private List<Usuario> determinarPodio(List<Oferta> ofertas) {
    Map<Long, Usuario> mejorOfertaPorUsuario = new LinkedHashMap<>();
    for (Oferta oferta : ofertas) {
      if (oferta.getUsuario() == null) {
        continue;
      }
      Long idUsuario = oferta.getUsuario().getId();
      if (!mejorOfertaPorUsuario.containsKey(idUsuario)) {
        mejorOfertaPorUsuario.put(idUsuario, oferta.getUsuario());
      }
    }
    List<Usuario> podio = new ArrayList<>(mejorOfertaPorUsuario.values());
    return podio.subList(0, Math.min(3, podio.size()));
  }

  private void validarSubasta(Subasta subasta) throws SubastaInvalidaExeption {
    validarNombreSubasta(subasta);
    validarPreciosSubasta(subasta);
    validarFechaCierreSubasta(subasta);
  }

  @Override
  public Subasta obtenerSubasta(Long id) {
    return repositorioSubasta.obtenerSubasta(id);
  }

  private void validarNombreSubasta(Subasta subasta) throws SubastaInvalidaExeption {
    if (subasta.getDetalle().getNombre() == null || subasta.getDetalle().getNombre().isBlank()) {
      throw new SubastaInvalidaExeption();
    }
  }

  private void validarPreciosSubasta(Subasta subasta) throws SubastaInvalidaExeption {
    if (subasta.getPrecioInicial() < 0) {
      throw new SubastaInvalidaExeption();
    }

    if (subasta.getPrecioMaximo() < 0 || subasta.getPrecioMaximo() < subasta.getPrecioInicial()) {
      throw new SubastaInvalidaExeption();
    }
  }

  private void validarFechaCierreSubasta(Subasta subasta) throws SubastaInvalidaExeption {
    if (subasta.getFechaCierre() == null) {
      throw new SubastaInvalidaExeption();
    }

    if (subasta.getFechaCierre().isBefore(LocalDateTime.now())) {
      throw new SubastaInvalidaExeption();
    }
  }

  @Override
  public List<Subasta> obtenerTodasLasSubastas() {
    return repositorioSubasta.obtenerTodasLasSubastas();
  }

  @Override
  public List<Subasta> obtenerSubastasDelCreador(Long idCreador) {
    return repositorioSubasta.buscarSubastasDelCreador(idCreador);
  }

  @Override
  public List<Subasta> obtenerSubastas(String busqueda) {
    List<Subasta> todasLasSubastas;
    if (busqueda != null && !busqueda.trim().isEmpty()) {
      todasLasSubastas = repositorioSubasta.buscarSubastas(busqueda);
    } else {
      todasLasSubastas = repositorioSubasta.obtenerTodasLasSubastas();
    }
    return todasLasSubastas
      .stream()
      .filter(s -> s.getEstadoSubasta() != EstadoSubasta.CERRADA)
      .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public void eliminarSubasta(Long subastaId, Usuario usuario) throws Exception {
    Subasta subasta = repositorioSubasta.obtenerSubasta(subastaId);
    if (subasta == null) {
      throw new Exception("Subasta no encontrada");
    }
    if (!subasta.esCreador(usuario)) {
      throw new Exception("No tenés permiso para eliminar esta subasta");
    }
    Oferta oferta = repositorioOferta.obtenerMejorOfertaPorSubasta(subastaId);
    if (oferta != null) {
      throw new SubastaConOfertasException();
    }
    repositorioSubasta.eliminarSubasta(subasta);
  }

  @Override
  public void editarSubasta(
    Long id,
    String nombre,
    String descripcion,
    String categoria,
    MultipartFile imagen,
    Usuario usuarioSolicitante
  ) throws SubastaNoEditableException {
    Subasta subasta = repositorioSubasta.obtenerSubasta(id);

    if (!subasta.esCreador(usuarioSolicitante)) {
      throw new SubastaNoEditableException();
    }

    subasta.getDetalle().setNombre(nombre);
    subasta.getDetalle().setDescripcion(descripcion);
    subasta.getDetalle().setCategoria(categoria);

    if (imagen != null && !imagen.isEmpty()) {
      try {
        subasta.getDetalle().setImagen(imagen.getBytes());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    repositorioSubasta.guardarSubasta(subasta);
  }
}
