package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioOfertaImpl implements ServicioOferta {

  private RepositorioSubasta repositorioSubasta;
  private RepositorioOferta repositorioOferta;
  private ServicioSubasta servicioSubasta;
  private RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioOfertaImpl(
    RepositorioSubasta repositorioSubasta,
    RepositorioOferta repositorioOferta,
    ServicioSubasta servicioSubasta,
    RepositorioUsuario repositorioUsuario
  ) {
    this.repositorioSubasta = repositorioSubasta;
    this.repositorioOferta = repositorioOferta;
    this.servicioSubasta = servicioSubasta;
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public void procesarOferta(long idSubasta, Oferta nuevaOferta)
    throws OfertaInvalidaException, SubastaNoEncontradaException {
    // Buscamos la subasta usando el repositorio
    Subasta subasta = repositorioSubasta.obtenerSubasta(idSubasta);

    // Verificamos que la subasta exista
    validarSubastaExistente(subasta);
    validarSubastaVigente(subasta);
    validarSubastaNoCerrada(subasta);

    // Validamos el monto de la oferta
    validarMontoOferta(nuevaOferta, subasta);

    Usuario usuario = repositorioUsuario.buscar("test@unlam.edu.ar");

    // Si no lanzó excepcion, vinculamos la oferta con la subasta y actualizamos el precio
    nuevaOferta.setSubasta(subasta);
    nuevaOferta.setUsuario(usuario);
    subasta.setPrecioActual(nuevaOferta.getMonto());

    // Ademas guardamos la oferta
    repositorioOferta.guardarOferta(nuevaOferta);

    servicioSubasta.verificarPrecioMaximo(idSubasta);
  }

  @Override
  public Oferta obtenerMejorOfertaPorSubasta(Long subastaId) {
    return repositorioOferta.obtenerMejorOfertaPorSubasta(subastaId);
  }

  @Override
  public List<Subasta> obtenerSubastasDondeParticipe(Long idUsuario) {
    return repositorioOferta.buscarSubastasDondeOferto(idUsuario);
  }

  private void validarSubastaExistente(Subasta subasta) throws SubastaNoEncontradaException {
    if (
      subasta == null ||
      subasta.getDetalle().getNombre() == null ||
      subasta.getDetalle().getNombre().isBlank()
    ) {
      throw new SubastaNoEncontradaException();
    }
  }

  private void validarSubastaVigente(Subasta subasta) throws OfertaInvalidaException {
    if (
      subasta.getFechaCierre() != null && subasta.getFechaCierre().isBefore(LocalDateTime.now())
    ) {
      throw new OfertaInvalidaException();
    }
  }

  private void validarSubastaNoCerrada(Subasta subasta) throws OfertaInvalidaException {
    if (subasta.getEstadoSubasta() == EstadoSubasta.CERRADA) {
      throw new OfertaInvalidaException();
    }
  }

  private void validarMontoOferta(Oferta nuevaOferta, Subasta subasta)
    throws OfertaInvalidaException {
    if (nuevaOferta.getMonto() <= subasta.getPrecioActual()) {
      throw new OfertaInvalidaException();
    }
  }
}
