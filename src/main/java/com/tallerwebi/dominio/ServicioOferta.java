package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import java.util.List;

public interface ServicioOferta {
  void procesarOferta(long idSubasta, Oferta nuevaOferta)
    throws OfertaInvalidaException, SubastaNoEncontradaException;

  Oferta obtenerMejorOfertaPorSubasta(Long subastaId);
  List<Subasta> obtenerSubastasDondeParticipe(Long idUsuario);
}
