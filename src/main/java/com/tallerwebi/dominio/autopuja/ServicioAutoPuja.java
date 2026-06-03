package com.tallerwebi.dominio.autopuja;

import com.tallerwebi.dominio.excepcion.AutoPujaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.dominio.usuario.Usuario;

public interface ServicioAutoPuja {
  void activarAutoPuja(long idSubasta, Usuario usuario, double montoMaximo)
    throws AutoPujaInvalidaException, SubastaNoEncontradaException;

  void procesarAutoPujas(Subasta subasta, Oferta ofertaManual);
}
