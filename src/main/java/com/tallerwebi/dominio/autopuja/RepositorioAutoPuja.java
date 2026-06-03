package com.tallerwebi.dominio.autopuja;

import java.util.List;

public interface RepositorioAutoPuja {
  void guardar(AutoPuja autoPuja);
  List<AutoPuja> obtenerAutoPujasActivasPorSubasta(Long subastaId);
}
