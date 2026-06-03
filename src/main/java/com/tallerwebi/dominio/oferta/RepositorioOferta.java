package com.tallerwebi.dominio.oferta;

import com.tallerwebi.dominio.subasta.Subasta;
import java.util.List;

public interface RepositorioOferta {
  void guardarOferta(Oferta oferta);
  List<Oferta> obtenerMejoresOfertasPorSubasta(Long subastaId);
  Oferta obtenerMejorOfertaPorSubasta(Long subastaId);
  List<Subasta> buscarSubastasDondeOferto(Long idUsuario);
}
