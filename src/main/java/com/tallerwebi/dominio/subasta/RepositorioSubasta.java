package com.tallerwebi.dominio.subasta;

import java.util.List;

public interface RepositorioSubasta {
  Subasta guardarSubasta(Subasta subasta);
  Subasta obtenerSubasta(Long id);
  List<Subasta> obtenerSubastasPorVencer();
  List<Subasta> obtenerTodasLasSubastas();
  List<Subasta> buscarSubastasDelCreador(Long idCreador);
  List<Subasta> buscarSubastas(String busqueda);
  void eliminarSubasta(Subasta subasta);
}
