package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.autopuja.ServicioAutoPuja;
import com.tallerwebi.dominio.excepcion.AutoPujaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestControladorAutoPujaTest {

  private ServicioAutoPuja servicioAutoPujaMock;
  private ServicioSubasta servicioSubastaMock;
  private RestControladorAutoPuja restControlador;
  private AutoPujaDTO autoPujaDTO;

  @BeforeEach
  public void init() {
    servicioAutoPujaMock = mock(ServicioAutoPuja.class);
    servicioSubastaMock = mock(ServicioSubasta.class);
    restControlador = new RestControladorAutoPuja(servicioAutoPujaMock, servicioSubastaMock);

    autoPujaDTO = new AutoPujaDTO();
    autoPujaDTO.setMontoMaximo(5000.0);
    autoPujaDTO.setUsuarioId(1L);
  }

  @Test
  public void alActivarAutoPujaValidaDeberiaRetornar200()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    ResponseEntity<String> respuesta = restControlador.activarAutoPuja(1L, autoPujaDTO);

    assertThat(respuesta.getStatusCode(), org.hamcrest.Matchers.is(HttpStatus.OK));
    verify(servicioAutoPujaMock, times(1)).activarAutoPuja(eq(1L), any(Usuario.class), eq(5000.0));
  }

  @Test
  public void alActivarAutoPujaInvalidaDeberiaRetornar400()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    doThrow(AutoPujaInvalidaException.class)
      .when(servicioAutoPujaMock)
      .activarAutoPuja(eq(1L), any(Usuario.class), eq(5000.0));

    ResponseEntity<String> respuesta = restControlador.activarAutoPuja(1L, autoPujaDTO);

    assertThat(respuesta.getStatusCode(), org.hamcrest.Matchers.is(HttpStatus.BAD_REQUEST));
    assertThat(respuesta.getBody(), containsStringIgnoringCase("no es válido"));
  }

  @Test
  public void alActivarAutoPujaEnSubastaInexistenteDeberiaRetornar404()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    doThrow(SubastaNoEncontradaException.class)
      .when(servicioAutoPujaMock)
      .activarAutoPuja(eq(1L), any(Usuario.class), eq(5000.0));

    ResponseEntity<String> respuesta = restControlador.activarAutoPuja(1L, autoPujaDTO);

    assertThat(respuesta.getStatusCode(), org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND));
    assertThat(respuesta.getBody(), containsStringIgnoringCase("no existe"));
  }
}
