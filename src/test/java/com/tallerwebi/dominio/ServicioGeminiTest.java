package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tallerwebi.dominio.recomendacion.ServicioGeminiImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

public class ServicioGeminiTest {

  private ServicioGeminiImpl servicioGemini;
  private RestTemplate restTemplateMock;
  private static final String PREGUNTA = "Hola?";
  private static final String RESPUESTA_ESPERADA = "Respuesta Gemini";
  private static final String JSON_RESPONSE =
    "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"" + RESPUESTA_ESPERADA + "\"}]}}]}";

  @BeforeEach
  public void init() {
    this.restTemplateMock = mock(RestTemplate.class);
    this.servicioGemini = new ServicioGeminiImpl(restTemplateMock);
  }

  @Test
  public void preguntarDeberiaRetornarRespuestaDesdeApi() throws JsonProcessingException {
    this.dadoQueLaApiResponde(JSON_RESPONSE);
    String respuesta = this.cuandoPregunto(PREGUNTA, null, false);
    this.entoncesLaRespuestaEs(respuesta, RESPUESTA_ESPERADA);
  }

  @Test
  public void preguntarDeberiaRetornarVacioSiNoHayCandidates() throws JsonProcessingException {
    this.dadoQueLaApiResponde("{\"error\": \"formato incorrecto\"}");
    String respuesta = this.cuandoPregunto(PREGUNTA, null, false);
    assertThat(respuesta, equalTo(""));
  }

  @Test
  public void preguntarDeberiaRetornarVacioSiElArrayCandidatesEstaVacio()
    throws JsonProcessingException {
    this.dadoQueLaApiResponde("{\"candidates\": []}");
    String respuesta = this.cuandoPregunto(PREGUNTA, null, false);
    assertThat(respuesta, equalTo(""));
  }

  @Test
  public void preguntarDeberiaRetornarMensajeDeErrorSiElJsonEsMalformado()
    throws JsonProcessingException {
    this.dadoQueLaApiResponde("{ esto no es json valido ");
    String respuesta = this.cuandoPregunto(PREGUNTA, null, false);
    assertThat(respuesta, startsWith("Error procesando respuesta de Gemini:"));
  }

  @Test
  public void configurarDeberiaEstablecerElContexto() throws JsonProcessingException {
    this.servicioGemini.configurar("Eres un experto en subastas");
    this.dadoQueLaApiResponde(JSON_RESPONSE);
    String respuesta = this.cuandoPregunto(PREGUNTA, null, false);
    this.entoncesLaRespuestaEs(respuesta, RESPUESTA_ESPERADA);
    this.entoncesLaPeticionContieneElContexto("Eres un experto en subastas");
  }

  @Test
  public void preguntarConReglaAdicionalPersistidaDeberiaAcumularContexto()
    throws JsonProcessingException {
    this.servicioGemini.setSystemInstruction("Base");
    this.dadoQueLaApiResponde(JSON_RESPONSE);
    this.cuandoPregunto(PREGUNTA, "Adicional", true);
    assertThat(this.servicioGemini.getSystemInstructions(), equalTo("Base. Adicional"));
  }

  @Test
  public void limpiarContextoDeberiaVaciarLasInstrucciones() {
    this.servicioGemini.setSystemInstruction("Contexto persistente");
    this.servicioGemini.limpiarContexto();
    assertThat(this.servicioGemini.getSystemInstructions(), equalTo(""));
  }

  private void dadoQueLaApiResponde(String json) {
    when(this.restTemplateMock.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
      .thenReturn(json);
  }

  private String cuandoPregunto(String mensaje, String contexto, boolean persistir)
    throws JsonProcessingException {
    return servicioGemini.preguntar(mensaje, contexto, persistir);
  }

  private void entoncesLaRespuestaEs(String actual, String esperada) {
    assertThat(actual, equalTo(esperada));
  }

  private void entoncesLaPeticionContieneElContexto(String contexto) {
    verify(restTemplateMock)
      .postForObject(
        anyString(),
        argThat((HttpEntity<String> entity) -> {
          String body = entity.getBody();
          return body != null && body.contains(contexto);
        }),
        eq(String.class)
      );
  }
}
