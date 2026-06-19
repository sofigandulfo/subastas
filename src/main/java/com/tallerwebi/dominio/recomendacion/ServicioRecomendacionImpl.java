package com.tallerwebi.dominio.recomendacion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tallerwebi.dominio.oferta.ServicioOferta;
import com.tallerwebi.dominio.subasta.EstadoSubasta;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ServicioRecomendacionImpl implements ServicioRecomendacion {

  private final ServicioSubasta servicioSubasta;
  private final ServicioOferta servicioOferta;
  private final ServicioGemini servicioGemini;

  @Autowired
  public ServicioRecomendacionImpl(
    ServicioSubasta servicioSubasta,
    ServicioOferta servicioOferta,
    ServicioGemini servicioGemini
  ) {
    this.servicioSubasta = servicioSubasta;
    this.servicioOferta = servicioOferta;
    this.servicioGemini = servicioGemini;
  }

  @Override
  public List<Subasta> obtenerRecomendaciones(Long usuarioId) {
    // Cambio el orden porque quiero que no figuren las subastas en las que el usuario ya pujó
    // Entonces primero obtenemos el historial

    List<Subasta> historial = servicioOferta.obtenerSubastasDondeParticipe(usuarioId);
    List<Long> idsPujadas = historial.stream().map(Subasta::getId).collect(Collectors.toList());

    List<Subasta> activas = servicioSubasta
      .obtenerTodasLasSubastas()
      .stream()
      .filter(s -> s.getEstadoSubasta() == EstadoSubasta.ACTIVA)
      .filter(s -> s.getCreador() == null || !s.getCreador().getId().equals(usuarioId))
      .filter(s -> !idsPujadas.contains(s.getId()))
      .collect(Collectors.toList());

    if (activas.isEmpty()) {
      return new ArrayList<>();
    }

    String prompt = construirPrompt(historial, activas);
    String respuesta = obtenerRespuestaGemini(prompt);

    return parsearRespuesta(respuesta, activas);
  }

  private String obtenerRespuestaGemini(String prompt) {
    try {
      return servicioGemini.preguntar(prompt, null, false);
    } catch (JsonProcessingException e) {
      return "";
    } catch (HttpClientErrorException e) {
      return "";
    }
  }

  @Override
  public List<Subasta> obtenerRecomendacionesPorIds(List<Long> ids) {
    return ids
      .stream()
      .map(id -> servicioSubasta.obtenerSubasta(id))
      .filter(s -> s != null && s.getEstadoSubasta() == EstadoSubasta.ACTIVA)
      .collect(Collectors.toList());
  }

  private String construirPrompt(List<Subasta> historial, List<Subasta> activas) {
    StringBuilder sb = new StringBuilder();
    sb.append("Sos un asistente de recomendaciones de subastas. ");
    sb.append("Devolvé ÚNICAMENTE los IDs de hasta 3 subastas recomendadas, ");
    sb.append("separados por coma, sin texto adicional.\n\n");
    sb.append("HISTORIAL DEL USUARIO (categorías donde participó):\n");
    if (historial.isEmpty()) {
      sb.append("Sin historial previo.\n");
    } else {
      String categorias = historial
        .stream()
        .map(s -> s.getDetalle().getCategoria())
        .distinct()
        .collect(Collectors.joining(", "));
      sb.append(categorias).append("\n");
    }
    sb.append("\nSUBASTAS ACTIVAS DISPONIBLES:\n");
    for (Subasta s : activas) {
      sb
        .append("ID:")
        .append(s.getId())
        .append(", Nombre:'")
        .append(s.getDetalle().getNombre())
        .append("'")
        .append(", Categoría:'")
        .append(s.getDetalle().getCategoria())
        .append("'")
        .append(", Precio:$")
        .append(s.getPrecioActual())
        .append("\n");
    }
    return sb.toString();
  }

  private List<Subasta> parsearRespuesta(String respuesta, List<Subasta> activas) {
    List<Subasta> recomendadas = new ArrayList<>();
    if (respuesta == null || respuesta.isBlank()) {
      return recomendadas;
    }
    String[] partes = respuesta.trim().split(",");
    for (String parte : partes) {
      agregarSiExiste(parte.trim(), activas, recomendadas);
    }
    return recomendadas;
  }

  private void agregarSiExiste(String parte, List<Subasta> activas, List<Subasta> recomendadas) {
    if (parte == null || !parte.matches("\\d+")) {
      return;
    }
    for (Subasta s : activas) {
      if (s.getId() != null && s.getId().equals(Long.parseLong(parte))) {
        recomendadas.add(s);
        break;
      }
    }
  }
}
