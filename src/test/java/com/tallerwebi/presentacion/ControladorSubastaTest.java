package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tallerwebi.dominio.ServicioSubasta;
import com.tallerwebi.dominio.Subasta;
import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorSubastaTest {

  private ControladorSubasta controladorSubasta;
  private Subasta subastaMock;
  private ServicioSubasta servicioSubastaMock;

  @BeforeEach
  public void init() {
    subastaMock = mock(Subasta.class);
    servicioSubastaMock = mock(ServicioSubasta.class);
    controladorSubasta = new ControladorSubasta(servicioSubastaMock);
  }

  @Test
  public void irACrearSubastaDeberiaRetornarVistaCrearSubastaConSubastaVacia() {
    ModelAndView modelAndView = controladorSubasta.irAlFormulario();
    // verifico que la vista es la corresta: cuando el usuario ingresa
    // /crear-subasta
    // se encuentra en el form para crear la subasta?
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    // verifico que haya un objeto subasta vacío en el modelo para cargarle datos
    assertThat(modelAndView.getModel().get("subasta"), instanceOf(Subasta.class));
  }

  @Test
  public void crearSubastaConDatosValidosDeberiaRedirigirAVistaDetalleSubasta()
    throws SubastaInvalidaExeption {
    ModelAndView modelAndView = controladorSubasta.crearSubasta(subastaMock);
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/detalle-subasta"));
    verify(servicioSubastaMock, times(1)).crearSubasta(subastaMock);
  }

  @Test
  public void crearSubastaConDatosInvalidosDeberiaVolverAlFormularioConError()
    throws SubastaInvalidaExeption {
    // preparacion: simulo que el servicio lanza una exception
    doThrow(SubastaInvalidaExeption.class).when(servicioSubastaMock).crearSubasta(subastaMock);
    // ejecucion
    ModelAndView modelAndView = controladorSubasta.crearSubasta(subastaMock);
    // valido que la vista sea la correcta y que se lanzo el mensaje de error
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Los datos ingresados son invalidos")
    );
  }

  @Test
  public void errorEnRegistrarSubastaDeberiaVolverAFormularioYMostrarError()
    throws SubastaInvalidaExeption {
    // preparacion: obligo a que se haga una runtimeException
    doThrow(RuntimeException.class).when(servicioSubastaMock).crearSubasta(subastaMock);
    // ejercucion
    ModelAndView modelAndView = controladorSubasta.crearSubasta(subastaMock);
    // valido
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Error al registrar nueva subasta")
    );
  }
}
