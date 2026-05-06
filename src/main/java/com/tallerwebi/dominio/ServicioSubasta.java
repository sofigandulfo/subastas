package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;

public interface ServicioSubasta {
  Subasta crearSubasta(Subasta subasta) throws SubastaInvalidaExeption;
  Subasta obtenerSubasta(Long id);
}
