package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tallerwebi.dominio.oferta.ServicioOferta;
import com.tallerwebi.dominio.recomendacion.ServicioGemini;
import com.tallerwebi.dominio.recomendacion.ServicioRecomendacion;
import com.tallerwebi.dominio.recomendacion.ServicioRecomendacionImpl;
import com.tallerwebi.dominio.subasta.DetalleSubasta;
import com.tallerwebi.dominio.subasta.EstadoSubasta;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.dominio.usuario.Usuario;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class ServicioRecomendacionTest {

  private ServicioSubasta servicioSubastaMock;
  private ServicioOferta servicioOfertaMock;
  private ServicioGemini servicioGeminiMock;
  private ServicioRecomendacion servicioRecomendacion;
  private Usuario usuario;

  @BeforeEach
  public void init() {
    servicioSubastaMock = mock(ServicioSubasta.class);
    servicioOfertaMock = mock(ServicioOferta.class);
    servicioGeminiMock = mock(ServicioGemini.class);
    servicioRecomendacion =
      new ServicioRecomendacionImpl(servicioSubastaMock, servicioOfertaMock, servicioGeminiMock);
    usuario = new Usuario();
    usuario.setId(1L);
  }

  @Test
  public void siNoHaySubastasActivasDeberiaRetornarListaVacia() throws JsonProcessingException {
    when(servicioSubastaMock.obtenerTodasLasSubastas()).thenReturn(List.of());

    List<Subasta> resultado = servicioRecomendacion.obtenerRecomendaciones(1L);

    assertThat(resultado, empty());
    verify(servicioGeminiMock, never()).preguntar(any(), any(), eq(false));
  }

  @Test
  public void siGeminiDevuelveIdValidoDeberiaRetornarLaSubastaCorrespondiente() throws Exception {
    Subasta subasta = crearSubastaActiva(5L, "Notebook", "Tecnologia", 2L); // creador es 2L
    when(servicioSubastaMock.obtenerTodasLasSubastas()).thenReturn(List.of(subasta));
    when(servicioOfertaMock.obtenerSubastasDondeParticipe(1L)).thenReturn(List.of());
    when(servicioGeminiMock.preguntar(any(), any(), eq(false))).thenReturn("5");

    List<Subasta> resultado = servicioRecomendacion.obtenerRecomendaciones(1L);

    assertThat(resultado, hasSize(1));
    assertThat(resultado.get(0), equalTo(subasta));
  }

  @Test
  public void siGeminiDevuelveVariosIdsDeberiaRetornarLasSubastasCorrespondientes()
    throws Exception {
    Subasta subasta1 = crearSubastaActiva(1L, "Notebook", "Tecnologia", 2L);
    Subasta subasta2 = crearSubastaActiva(2L, "Guitarra", "Musica", 2L);
    Subasta subasta3 = crearSubastaActiva(3L, "Bicicleta", "Deportes", 2L);
    when(servicioSubastaMock.obtenerTodasLasSubastas())
      .thenReturn(List.of(subasta1, subasta2, subasta3));
    when(servicioOfertaMock.obtenerSubastasDondeParticipe(1L)).thenReturn(List.of());
    when(servicioGeminiMock.preguntar(any(), any(), eq(false))).thenReturn("1,3");

    List<Subasta> resultado = servicioRecomendacion.obtenerRecomendaciones(1L);

    assertThat(resultado, hasSize(2));
    assertThat(resultado.get(0), equalTo(subasta1));
    assertThat(resultado.get(1), equalTo(subasta3));
  }

  @Test
  public void siGeminiDevuelveTextoInvalidoDeberiaRetornarListaVacia() throws Exception {
    Subasta subasta = crearSubastaActiva(1L, "Notebook", "Tecnologia", 2L);
    when(servicioSubastaMock.obtenerTodasLasSubastas()).thenReturn(List.of(subasta));
    when(servicioOfertaMock.obtenerSubastasDondeParticipe(1L)).thenReturn(List.of());
    when(servicioGeminiMock.preguntar(any(), any(), eq(false)))
      .thenReturn("no puedo responder eso");

    List<Subasta> resultado = servicioRecomendacion.obtenerRecomendaciones(1L);

    assertThat(resultado, empty());
  }

  @Test
  public void lasSubastasDelPropioUsuarioNoDeberianAparecerEnLasRecomendaciones() throws Exception {
    Subasta subastaPropia = crearSubastaActiva(1L, "Notebook", "Tecnologia", 1L);
    Subasta subastaAjena = crearSubastaActiva(2L, "Guitarra", "Musica", 2L);
    when(servicioSubastaMock.obtenerTodasLasSubastas())
      .thenReturn(List.of(subastaPropia, subastaAjena));
    when(servicioOfertaMock.obtenerSubastasDondeParticipe(1L)).thenReturn(List.of());
    when(servicioGeminiMock.preguntar(any(), any(), eq(false))).thenReturn("2");

    List<Subasta> resultado = servicioRecomendacion.obtenerRecomendaciones(1L);

    assertThat(resultado, hasSize(1));
    assertThat(resultado.get(0), equalTo(subastaAjena));
  }

  @Test
  public void cuandoLlamoAObtenerRecomendacionPorIdsObtengoLasActivas() {
    Subasta activa = crearSubastaActiva(1L, "Pantuflas", "Ropa", 1L);
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(activa);

    List<Subasta> resultado = servicioRecomendacion.obtenerRecomendacionesPorIds(List.of(1L));

    assertThat(resultado, hasSize(1));
    assertThat(resultado.get(0), equalTo(activa));
  }

  @Test
  public void cuandoLlamoAObtenerRecomendacionPorIdsDescartaLasInactivas() {
    Subasta cerrada = crearSubastaActiva(1L, "Lego", "Entretenimiento", 1L);
    cerrada.setEstadoSubasta(EstadoSubasta.CERRADA);
    when(servicioSubastaMock.obtenerSubasta(2L)).thenReturn(cerrada);
    List<Subasta> resultado = servicioRecomendacion.obtenerRecomendacionesPorIds(List.of(2L));
    assertThat(resultado, empty());
  }

  @Test
  public void siGeminiLanzaUnHttpClientErrorExceptionDeberiaRetornarListaVacia() throws Exception {
    Subasta subasta = crearSubastaActiva(1L, "Notebook", "Tecnologia", 2L);
    when(servicioSubastaMock.obtenerTodasLasSubastas()).thenReturn(List.of(subasta));
    when(servicioOfertaMock.obtenerSubastasDondeParticipe(1L)).thenReturn(List.of());
    when(servicioGeminiMock.preguntar(any(), any(), eq(false)))
      .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

    List<Subasta> resultado = servicioRecomendacion.obtenerRecomendaciones(1L);

    assertThat(resultado, empty());
  }

  private Subasta crearSubastaActiva(Long id, String nombre, String categoria, Long idCreador) {
    Usuario creador = new Usuario();
    creador.setId(idCreador);
    Subasta subasta = new Subasta(nombre, "desc", 1000.0, 5000.0, categoria, "nuevo");
    subasta.setId(id);
    subasta.setEstadoSubasta(EstadoSubasta.ACTIVA);
    subasta.setCreador(creador);
    return subasta;
  }
}
