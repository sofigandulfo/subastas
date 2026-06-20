package com.tallerwebi.dominio.recomendacion;

import com.tallerwebi.dominio.subasta.Subasta;
import java.util.List;

public interface ServicioRecomendacion {
  List<Subasta> obtenerRecomendaciones(Long usuarioId);
  List<Subasta> obtenerRecomendacionesPorIds(List<Long> ids);
}
