package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.recomendacion.ServicioRecomendacion;
import com.tallerwebi.dominio.subasta.Subasta;
import java.util.List;
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

    List<Subasta> recomendaciones = (List<Subasta>) request
      .getSession()
      .getAttribute("RECOMENDACIONES");

    if (recomendaciones == null) {
      recomendaciones = servicioRecomendacion.obtenerRecomendaciones(usuarioId);
      request.getSession().setAttribute("RECOMENDACIONES", recomendaciones);
    }

    List<RecomendacionDTO> dto = recomendaciones
      .stream()
      .map(RecomendacionDTO::new)
      .collect(java.util.stream.Collectors.toList());

    return ResponseEntity.ok(dto);
  }
}
