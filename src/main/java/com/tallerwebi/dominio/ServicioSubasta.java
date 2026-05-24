package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import org.springframework.web.multipart.MultipartFile;

public interface ServicioSubasta {
  Subasta obtenerSubasta(Long id);
  Subasta crearSubasta(Subasta subasta, MultipartFile imagen, Usuario usuario)
    throws SubastaInvalidaExeption;
  void verificarPrecioMaximo(Long subastaId);
  void cerrarSubastasPorTiempo();
}
