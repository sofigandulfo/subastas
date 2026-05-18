package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioSubasta {
  Subasta guardarSubasta(Subasta subasta);
  Subasta obtenerSubasta(Long id);
  List<Subasta> obtenerSubastasPorVencer();
}
