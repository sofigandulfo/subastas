package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.recomendacion.ServicioRecomendacion;
import com.tallerwebi.dominio.subasta.Subasta;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ControladorRecomendacion {

  private final ServicioRecomendacion servicioRecomendacion;

  @Autowired
  public ControladorRecomendacion(ServicioRecomendacion servicioRecomendacion) {
    this.servicioRecomendacion = servicioRecomendacion;
  }

  @GetMapping("/recomendaciones")
  public ResponseEntity<?> obtenerRecomendaciones(HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("USUARIO_ID");
    if (usuarioId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    List<Long> idsEnCache = (List<Long>) request.getSession().getAttribute("RECOMENDACIONES_IDS");
    List<Subasta> recomendaciones;

    if (idsEnCache != null && !idsEnCache.isEmpty()) {
      recomendaciones = servicioRecomendacion.obtenerRecomendacionesPorIds(idsEnCache);
      List<Long> idsActivos = recomendaciones
        .stream()
        .map(Subasta::getId)
        .collect(Collectors.toList());
      request.getSession().setAttribute("RECOMENDACIONES_IDS", idsActivos);
    } else {
      recomendaciones = servicioRecomendacion.obtenerRecomendaciones(usuarioId);
      if (!recomendaciones.isEmpty()) {
        List<Long> ids = recomendaciones.stream().map(Subasta::getId).collect(Collectors.toList());
        request.getSession().setAttribute("RECOMENDACIONES_IDS", ids);
      }
    }

    List<RecomendacionDTO> dto = recomendaciones
      .stream()
      .map(RecomendacionDTO::new)
      .collect(java.util.stream.Collectors.toList());

    return ResponseEntity.ok(dto);
  }
}
