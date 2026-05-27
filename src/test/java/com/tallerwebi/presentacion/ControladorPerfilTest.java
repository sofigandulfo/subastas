package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioOferta;
import com.tallerwebi.dominio.ServicioSubasta;
import com.tallerwebi.dominio.Subasta;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPerfilTest {

  private ControladorPerfil controladorPerfil;
  private ServicioSubasta servicioSubastaMock;
  private ServicioOferta servicioOfertaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    // Mockeamos los servicios que va a usar nuestro controlador
    servicioSubastaMock = mock(ServicioSubasta.class);
    servicioOfertaMock = mock(ServicioOferta.class);

    // Instanciamos el controlador que vamos a testear
    controladorPerfil = new ControladorPerfil(servicioSubastaMock, servicioOfertaMock);

    // Mockeamos la sesión web
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
  }

  @Test
  public void siUnUsuarioIntentaVerElPerfilSinEstarLogueadoDeberiaSerRedirigidoAlLogin() {
    // Preparación: Simulamos que NO hay id de usuario en la sesión
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

    // Ejecución: Llamamos al método
    ModelAndView mav = controladorPerfil.verPerfil(requestMock);

    // Validación: Verificamos que redirija al login
    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));

    // Verificamos que NUNCA se haya llamado a los servicios porque cortó antes
    verify(servicioSubastaMock, never()).obtenerSubastasDelCreador(anyLong());
  }

  @Test
  public void siElUsuarioEstaLogueadoDeberiaVerSuPerfilConSusVentasYSusPujas() {
    // Preparación
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    // Simulamos que el usuario tiene 1 venta y 2 pujas
    List<Subasta> ventasSimuladas = new ArrayList<>();
    ventasSimuladas.add(new Subasta());

    List<Subasta> pujasSimuladas = new ArrayList<>();
    pujasSimuladas.add(new Subasta());
    pujasSimuladas.add(new Subasta());

    // Le enseñamos a los mocks qué tienen que devolver cuando el controlador los llame
    when(servicioSubastaMock.obtenerSubastasDelCreador(idUsuario)).thenReturn(ventasSimuladas);
    when(servicioOfertaMock.obtenerSubastasDondeParticipe(idUsuario)).thenReturn(pujasSimuladas);

    // Ejecución
    ModelAndView mav = controladorPerfil.verPerfil(requestMock);

    // Validación
    // 1. Verificamos que devuelva la vista "perfil"
    assertThat(mav.getViewName(), equalToIgnoringCase("perfil"));

    // 2. Verificamos que el modelo tenga el ID del usuario
    assertThat(mav.getModel().get("usuarioId"), equalTo(idUsuario));

    // 3. Verificamos que las listas de ventas y pujas hayan llegado intactas a la vista
    assertThat(mav.getModel().get("misVentas"), equalTo(ventasSimuladas));
    assertThat(mav.getModel().get("misPujas"), equalTo(pujasSimuladas));
  }
}
