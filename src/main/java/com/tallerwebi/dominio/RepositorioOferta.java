package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioOferta {
  void guardarOferta(Oferta oferta);
  List<Oferta> obtenerMejoresOfertasPorSubasta(Long subastaId);
  Oferta obtenerMejorOfertaPorSubasta(Long subastaId);
}
