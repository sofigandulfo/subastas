package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;

public interface ServicioSubasta {
  void crearSubasta(Subasta subasta) throws SubastaInvalidaExeption;
}
