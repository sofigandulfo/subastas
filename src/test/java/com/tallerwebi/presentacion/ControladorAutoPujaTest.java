package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.autopuja.ServicioAutoPuja;
import com.tallerwebi.dominio.excepcion.AutoPujaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.dominio.usuario.Usuario;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorAutoPujaTest {

  private ServicioAutoPuja servicioAutoPujaMock;
  private ServicioSubasta servicioSubastaMock;
  private ControladorAutoPuja controladorAutoPuja;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private AutoPujaDTO autoPujaDTO;
  private Subasta subastaMock;

  @BeforeEach
  public void init() {
    servicioAutoPujaMock = mock(ServicioAutoPuja.class);
    servicioSubastaMock = mock(ServicioSubasta.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);

    // instanciamos el controlador inyectando los mocks
    controladorAutoPuja = new ControladorAutoPuja(servicioAutoPujaMock, servicioSubastaMock);

    // preparamos datos genéricos para los tests
    autoPujaDTO = new AutoPujaDTO();
    autoPujaDTO.setMontoMaximo(5000.0);
    subastaMock = mock(Subasta.class);
  }

  @Test
  public void siNoHaySesionAlIrAFormularioDeberiaRedirigirAlLogin() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

    ModelAndView mav = controladorAutoPuja.irAFormularioAutoPuja(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void alIrAFormularioDeberiaRetornarVistaAutoPujaConDTOSVacioYSubasta() {
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);

    ModelAndView mav = controladorAutoPuja.irAFormularioAutoPuja(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("auto-puja"));
    assertThat(mav.getModel().get("subasta"), instanceOf(Subasta.class));
    assertThat(mav.getModel().get("autoPujaDTO"), instanceOf(AutoPujaDTO.class));
  }

  @Test
  public void alActivarAutoPujaValidaDeberiaRedirigirAlDetalle()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    ModelAndView mav = controladorAutoPuja.activarAutoPuja(1L, autoPujaDTO, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/detalle-subasta?id=1"));
    verify(servicioAutoPujaMock, times(1)).activarAutoPuja(eq(1L), any(Usuario.class), eq(5000.0));
  }

  @Test
  public void alActivarAutoPujaInvalidaDeberiaVolverAlFormularioConError()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    // hacemos que el mock simule el error de validación
    doThrow(AutoPujaInvalidaException.class)
      .when(servicioAutoPujaMock)
      .activarAutoPuja(eq(1L), any(Usuario.class), eq(5000.0));

    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);

    ModelAndView mav = controladorAutoPuja.activarAutoPuja(1L, autoPujaDTO, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("auto-puja"));
    assertThat(
      mav.getModel().get("error").toString(),
      equalToIgnoringCase("El monto máximo ingresado no es válido para activar la auto-puja.")
    );
  }

  @Test
  public void alActivarAutoPujaEnSubastaInexistenteDeberiaVolverAlFormularioConError()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    doThrow(SubastaNoEncontradaException.class)
      .when(servicioAutoPujaMock)
      .activarAutoPuja(eq(1L), any(Usuario.class), eq(5000.0));

    ModelAndView mav = controladorAutoPuja.activarAutoPuja(1L, autoPujaDTO, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("auto-puja"));
    assertThat(
      mav.getModel().get("error").toString(),
      equalToIgnoringCase("La subasta a la que intenta acceder no existe.")
    );
  }
}
