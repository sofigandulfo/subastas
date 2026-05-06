package com.tallerwebi.dominio;

public interface RepositorioSubasta {
  Subasta guardarSubasta(Subasta subasta);
  Subasta obtenerSubasta(Long id);
}
