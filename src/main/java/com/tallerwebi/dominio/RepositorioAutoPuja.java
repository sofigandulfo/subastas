package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioAutoPuja {
  void guardar(AutoPuja autoPuja);
  List<AutoPuja> obtenerAutoPujasActivasPorSubasta(Long subastaId);
}
