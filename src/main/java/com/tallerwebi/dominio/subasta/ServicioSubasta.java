package com.tallerwebi.dominio.subasta;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import com.tallerwebi.dominio.usuario.Usuario;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ServicioSubasta {
  Subasta obtenerSubasta(Long id);
  Subasta crearSubasta(Subasta subasta, MultipartFile imagen, Usuario usuario)
    throws SubastaInvalidaExeption;
  void verificarPrecioMaximo(Long subastaId);
  void cerrarSubastasPorTiempo();
  List<Subasta> obtenerTodasLasSubastas();
  List<Subasta> obtenerSubastasDelCreador(Long idCreador);
  List<Subasta> obtenerSubastas(String busqueda);
  void eliminarSubasta(Long subastaId, Usuario usuarioCreador) throws Exception;
}
