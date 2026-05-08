package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;

public interface ServicioOferta {
  void procesarOferta(long idSubasta, Oferta nuevaOferta)
    throws OfertaInvalidaException, SubastaNoEncontradaException;
}
