package com.tallerwebi.dominio.oferta;

import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.dominio.usuario.Usuario;
import java.util.List;

public interface ServicioOferta {
  void procesarOferta(long idSubasta, Oferta nuevaOferta, Usuario ofertante)
    throws OfertaInvalidaException, SubastaNoEncontradaException;

  Oferta obtenerMejorOfertaPorSubasta(Long subastaId);
  List<Subasta> obtenerSubastasDondeParticipe(Long idUsuario);
  List<Oferta> obtenerMejoresOfertasPorSubasta(Long subastaId);
}
