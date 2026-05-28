package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorOfertaTest {

  private ServicioOferta servicioOfertaMock;
  private ServicioSubasta servicioSubastaMock;
  private ControladorOferta controladorOferta;
  private OfertaDTO ofertaDTO;
  private Oferta ofertaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    // Mockeamos ambos servicios porque el controlador los va a necesitar
    servicioOfertaMock = mock(ServicioOferta.class);
    servicioSubastaMock = mock(ServicioSubasta.class);
    controladorOferta = new ControladorOferta(servicioOfertaMock, servicioSubastaMock);
    ofertaMock = mock(Oferta.class);
    ofertaDTO = mock(OfertaDTO.class);
    when(ofertaDTO.entidad()).thenReturn(ofertaMock);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
  }

  @Test
  public void alIrAFormularioDeOfertaDeberiaRetornarLaVistaOfertaConUnaOfertaVaciaYLosDatosDeLaSubasta() {
    // Preparación
    Long idSubasta = 1L;
    Subasta subastaMock = new Subasta();
    subastaMock.setId(idSubasta);

    // Cuando el controlador pida la subasta para mandarla a la vista, le damos nuestra subasta mockeada
    when(servicioSubastaMock.obtenerSubasta(idSubasta)).thenReturn(subastaMock);

    // Ejecución (simulamos que el usuario entra a la URL por GET)
    ModelAndView mav = controladorOferta.irAFormularioOferta(idSubasta, requestMock);

    // Validación
    // 1. Verificamos que el controlador nos devuelva el nombre de la vista correcta ("oferta")
    assertThat(mav.getViewName(), equalToIgnoringCase("oferta"));

    // 2. Verificamos que la vista haya recibido el objeto subasta (para mostrar el precio actual)
    assertThat(mav.getModel().get("subasta"), instanceOf(Subasta.class));

    // 3. Verificamos que la vista haya recibido un objeto Oferta vacío (para atajar el th:object)
    assertThat(mav.getModel().get("oferta"), instanceOf(OfertaDTO.class));
  }

  @Test
  public void alRealizarUnaOfertaValidaDeberiaRedirigirAlDetalleDeLaSubasta()
    throws OfertaInvalidaException, SubastaNoEncontradaException {
    // Preparación
    Long idSubasta = 1L;

    // Ejecución
    ModelAndView mav = controladorOferta.realizarOferta(idSubasta, ofertaDTO, requestMock);

    // Validación
    // 1. Verificamos que redirija al detalle
    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/detalle-subasta?id=" + idSubasta));

    // 2. Verificamos que el controlador realmente le pidió al servicio que procese la oferta
    verify(servicioOfertaMock, times(1))
      .procesarOferta(eq(idSubasta), eq(ofertaMock), any(Usuario.class));
  }

  @Test
  public void alRealizarUnaOfertaInvalidaDeberiaVolverAlFormularioConError()
    throws OfertaInvalidaException, SubastaNoEncontradaException {
    // Preparación
    Long idSubasta = 1L;
    Subasta subastaMock = new Subasta();

    // Hacemos que el servicio simule que falló y tire la excepción
    doThrow(OfertaInvalidaException.class)
      .when(servicioOfertaMock)
      .procesarOferta(eq(idSubasta), eq(ofertaMock), any(Usuario.class));

    // Necesitamos devolver la subasta mockeada para recargar la página sin que pinche el HTML
    when(servicioSubastaMock.obtenerSubasta(idSubasta)).thenReturn(subastaMock);

    // Ejecución
    ModelAndView mav = controladorOferta.realizarOferta(idSubasta, ofertaDTO, requestMock);

    // Validación
    // Verificamos que nos deje en la vista "oferta" (el formulario) y NO redirija
    assertThat(mav.getViewName(), equalToIgnoringCase("oferta"));
    // Verificamos que haya mandado un mensaje de error a la vista
    assertThat(
      mav.getModel().get("error").toString(),
      equalToIgnoringCase("La oferta ingresada no es válida.")
    );
  }

  @Test
  public void cuandoElUsuarioNoInicioSesionEIntentaOfertarDeberiaRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);
    ModelAndView modelAndView = controladorOferta.irAFormularioOferta(1L, requestMock);
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }
}
