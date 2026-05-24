package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ServicioSubastaImpl implements ServicioSubasta {

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
        List<Oferta> mejoresOfertas = repositorioOferta.obtenerMejoresOfertasPorSubasta(
          subasta.getId()
        );
        List<Usuario> podio = new ArrayList<>();
        for (int i = 0; i < Math.min(3, mejoresOfertas.size()); i++) {
          podio.add(mejoresOfertas.get(i).getUsuario());
        }
        subasta.setPodio(podio);
        subasta.setEstadoSubasta(EstadoSubasta.CERRADA);
        repositorioSubasta.guardarSubasta(subasta);
      }
    }
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
}
