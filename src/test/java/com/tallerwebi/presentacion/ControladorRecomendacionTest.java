package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.recomendacion.ServicioRecomendacion;
import com.tallerwebi.dominio.subasta.EstadoSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControladorRecomendacionTest {

  private ControladorRecomendacion controladorRecomendacion;
  private ServicioRecomendacion servicioRecomendacionMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioRecomendacionMock = mock(ServicioRecomendacion.class);
    controladorRecomendacion = new ControladorRecomendacion(servicioRecomendacionMock);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);
  }

  @Test
  public void siElUsuarioNoEstaLogueadoEsRedirigidoAlLogin() {
    ResponseEntity<?> respuesta = controladorRecomendacion.obtenerRecomendaciones(requestMock);
    assertThat(respuesta.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void siElUsuarioNoTieneRecomendacionGuardadaEnCacheSeLlamaAServicioRecomendacionSeGuardaYMuestraLaRecomendacion() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(sessionMock.getAttribute("RECOMENDACIONES_IDS")).thenReturn(null);

    Subasta subasta = crearSubastaActiva(5L);
    when(servicioRecomendacionMock.obtenerRecomendaciones(1L)).thenReturn(List.of(subasta));

    ResponseEntity<?> respuesta = controladorRecomendacion.obtenerRecomendaciones(requestMock);

    assertThat(respuesta.getStatusCode(), equalTo(HttpStatus.OK));
    List<RecomendacionDTO> body = (List<RecomendacionDTO>) respuesta.getBody();
    assertThat(body, hasSize(1));
    verify(sessionMock).setAttribute(eq("RECOMENDACIONES_IDS"), any());
  }

  @Test
  public void siElUsuarioTieneRecomendacionGuardadaEnCacheSeLlamaAServicioRecomendacionSeEvaluaYMuestraLaRecomendacion() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(sessionMock.getAttribute("RECOMENDACIONES_IDS")).thenReturn(List.of(5L));

    Subasta subasta = crearSubastaActiva(5L);
    when(servicioRecomendacionMock.obtenerRecomendacionesPorIds(List.of(5L)))
      .thenReturn(List.of(subasta));

    ResponseEntity<?> respuesta = controladorRecomendacion.obtenerRecomendaciones(requestMock);

    assertThat(respuesta.getStatusCode(), equalTo(HttpStatus.OK));
    List<RecomendacionDTO> body = (List<RecomendacionDTO>) respuesta.getBody();
    assertThat(body, hasSize(1));
    verify(servicioRecomendacionMock, never()).obtenerRecomendaciones(anyLong());
  }

  @Test
  public void siElUsuarioTieneRecomendacionGuardadaEnCacheSeLlamaAServicioRecomendacionSeEvaluaYMuestraLaRecomendacionActiva() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(sessionMock.getAttribute("RECOMENDACIONES_IDS")).thenReturn(List.of(5L, 6L));

    Subasta activa = crearSubastaActiva(5L);
    when(servicioRecomendacionMock.obtenerRecomendacionesPorIds(List.of(5L, 6L)))
      .thenReturn(List.of(activa));

    ResponseEntity<?> respuesta = controladorRecomendacion.obtenerRecomendaciones(requestMock);

    assertThat(respuesta.getStatusCode(), equalTo(HttpStatus.OK));
    List<RecomendacionDTO> body = (List<RecomendacionDTO>) respuesta.getBody();
    assertThat(body, hasSize(1));
    assertThat(body.get(0).getId(), equalTo(5L));
  }

  @Test
  public void siLaCacheEstaVaciaSeRegeneraLlamandoAObtenerRecomendaciones() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(sessionMock.getAttribute("RECOMENDACIONES_IDS")).thenReturn(List.of());

    Subasta subasta = crearSubastaActiva(5L);
    when(servicioRecomendacionMock.obtenerRecomendaciones(1L)).thenReturn(List.of(subasta));

    ResponseEntity<?> respuesta = controladorRecomendacion.obtenerRecomendaciones(requestMock);

    assertThat(respuesta.getStatusCode(), equalTo(HttpStatus.OK));
    List<RecomendacionDTO> body = (List<RecomendacionDTO>) respuesta.getBody();
    assertThat(body, hasSize(1));
    verify(servicioRecomendacionMock).obtenerRecomendaciones(1L);
    verify(servicioRecomendacionMock, never()).obtenerRecomendacionesPorIds(any());
  }

  private Subasta crearSubastaActiva(Long id) {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setId(id);
    subasta.setEstadoSubasta(EstadoSubasta.ACTIVA);
    return subasta;
  }
}
