package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioOfertaImpl implements ServicioOferta {

  private RepositorioSubasta repositorioSubasta;
  private RepositorioOferta repositorioOferta;

  @Autowired
  public ServicioOfertaImpl(
    RepositorioSubasta repositorioSubasta,
    RepositorioOferta repositorioOferta
  ) {
    this.repositorioSubasta = repositorioSubasta;
    this.repositorioOferta = repositorioOferta;
  }

  @Override
  public void procesarOferta(long idSubasta, Oferta nuevaOferta)
    throws OfertaInvalidaException, SubastaNoEncontradaException {
    // Buscamos la subasta usando el repositorio
    Subasta subasta = repositorioSubasta.obtenerSubasta(idSubasta);

    // Verificamos que la subasta exista
    if (subasta == null || subasta.getNombre() == null || subasta.getNombre().isBlank()) {
      throw new SubastaNoEncontradaException();
    }

    // Validamos el monto de la oferta
    if (nuevaOferta.getMonto() <= subasta.getPrecioActual()) {
      throw new OfertaInvalidaException();
    }

    // Si no lanzó excepcion, vinculamos la oferta con la subasta y actualizamos el precio
    nuevaOferta.setSubasta(subasta);
    subasta.setPrecioActual(nuevaOferta.getMonto());

    // Ademas guardamos la oferta
    repositorioOferta.guardarOferta(nuevaOferta);
  }
}
