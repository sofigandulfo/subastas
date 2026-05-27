package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.AutoPujaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;

public interface ServicioAutoPuja {
  void activarAutoPuja(long idSubasta, Usuario usuario, double montoMaximo)
    throws AutoPujaInvalidaException, SubastaNoEncontradaException;

  void procesarAutoPujas(Subasta subasta, Oferta ofertaManual);
}
