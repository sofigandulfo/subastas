package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import com.tallerwebi.dominio.excepcion.SubastaNoEditableException;
import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.oferta.ServicioOferta;
import com.tallerwebi.dominio.subasta.DetalleSubasta;
import com.tallerwebi.dominio.subasta.EstadoSubasta;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.dominio.usuario.Usuario;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

public class ControladorSubastaTest {

  private ControladorSubasta controladorSubasta;
  private Subasta subastaMock;
  private ServicioSubasta servicioSubastaMock;
  private MultipartFile imagenMock;
  private SubastaDTO subastaDTO;
  private DetalleSubasta detalleMock;
  private ServicioOferta servicioOfertaMock;
  private Usuario usuarioCreador;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    subastaDTO = mock(SubastaDTO.class);
    subastaMock = mock(Subasta.class);
    detalleMock = mock(DetalleSubasta.class);
    when(subastaMock.getDetalle()).thenReturn(detalleMock);
    servicioSubastaMock = mock(ServicioSubasta.class);
    servicioOfertaMock = mock(ServicioOferta.class);
    controladorSubasta = new ControladorSubasta(servicioSubastaMock, servicioOfertaMock);
    imagenMock = mock(MultipartFile.class);
    usuarioCreador = new Usuario();
    usuarioCreador.setId(1L);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);
  }

  @Test
  public void irACrearSubastaConSesionDeberiaRetornarVistaCrearSubastaConSubastaVacia() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    ModelAndView modelAndView = controladorSubasta.irAlFormulario(requestMock);
    // verifico que la vista es la corresta: cuando el usuario ingresa
    // /crear-subasta
    // se encuentra en el form para crear la subasta?
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    // verifico que haya un objeto subasta vacío en el modelo para cargarle datos
    assertThat(modelAndView.getModel().get("subasta"), instanceOf(SubastaDTO.class));
  }

  @Test
  public void irACrearSubastaSinSesionMeDeberiaRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);
    ModelAndView modelAndView = controladorSubasta.irAlFormulario(requestMock);
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void crearSubastaConDatosValidosDeberiaRedirigirAVistaDetalleSubasta()
    throws SubastaInvalidaExeption {
    when(subastaDTO.entidad()).thenReturn(subastaMock);
    when(servicioSubastaMock.crearSubasta(subastaMock, imagenMock, usuarioCreador))
      .thenReturn(subastaMock);
    when(subastaMock.getId()).thenReturn(1L);
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);

    ModelAndView modelAndView = controladorSubasta.crearSubasta(
      subastaDTO,
      imagenMock,
      requestMock
    );
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/detalle-subasta?id=1"));
    verify(servicioSubastaMock, times(1)).crearSubasta(subastaMock, imagenMock, usuarioCreador);
  }

  @Test
  public void crearSubastaConDatosInvalidosDeberiaVolverAlFormularioConError()
    throws SubastaInvalidaExeption {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(subastaDTO.entidad()).thenReturn(subastaMock);
    // preparacion: simulo que el servicio lanza una exception
    doThrow(SubastaInvalidaExeption.class)
      .when(servicioSubastaMock)
      .crearSubasta(subastaMock, imagenMock, usuarioCreador);
    // ejecucion
    ModelAndView modelAndView = controladorSubasta.crearSubasta(
      subastaDTO,
      imagenMock,
      requestMock
    );
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
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    // preparacion: obligo a que se haga una runtimeException
    doThrow(RuntimeException.class)
      .when(servicioSubastaMock)
      .crearSubasta(subastaMock, imagenMock, usuarioCreador);
    // ejercucion
    ModelAndView modelAndView = controladorSubasta.crearSubasta(
      subastaDTO,
      imagenMock,
      requestMock
    );
    // valido
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Error al registrar nueva subasta")
    );
  }

  @Test
  public void verDetalleConIdValidoDeberiaRetornarVistaDetalleConSubasta() {
    // preparacion->No me importa la logica del SERVICIO por lo que mockeo
    // lo que debe devolver cuando le pida la subasta con id 1.
    // Simulo: "existe una subasta con id 1 en la BD"
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock); // cuando le pido al servicio una subasta con
    // id 1L me retorna una subasta falsa.
    // ejecucion ->Llamo al CONTROLADOR
    ModelAndView modelAndView = controladorSubasta.verDetalle(1L, requestMock); // mostrame la vista y el modelo de subasta id 1L
    // valido-> Verifico que el controlador devuelva la vista y un objeto de tipo
    // subasta en el modelo
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("detalle-subasta"));
    assertThat(modelAndView.getModel().get("subasta"), instanceOf(Subasta.class));
  }

  @Test
  public void verDetalleConIdInexistenteDeberiaVolverAlFormularioConError() {
    // preparacion-> El servicio devuelve null al llamar un id por ejemplo 200L
    when(servicioSubastaMock.obtenerSubasta(200L)).thenReturn(null);
    // ejecucion
    ModelAndView modelAndView = controladorSubasta.verDetalle(200L, requestMock);
    // verifico que no paso a la vista de detalle con una subasta y me haya llevado
    // al formulario con error
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Subasta no encontrada")
    );
  }

  // test para imagen dinamica:

  @Test
  public void verDetalleConSubastaQuetieneImagenDeberiaAgregarImagenBase64AlModeloEnFormaDeTexto() {
    // PREPARACION cuando llame al servicio con long 1 retorna la subasta mockeada
    // cuando llame a la subasta con su imagen retorna imagen mockeada
    byte[] imagenBytes = new byte[] { 1, 2, 3 };
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(detalleMock.getImagen()).thenReturn(imagenBytes);
    // ejecuto el modelo
    ModelAndView modelAndView = controladorSubasta.verDetalle(1L, requestMock);
    // verifico haber obtenido en el modelo clave ImagenBase64 y es un texto
    assertThat(modelAndView.getModel().get("imagenBase64"), instanceOf((String.class)));
  }

  @Test
  public void verDetalleConSubastaSinImagenNoDeberiaAgregarImagenBase64AlModelo() {
    // PREPARACION cuando llame al servicio con long 1 retorna la subasta mockeada
    // cuando llame a la subasta con su imagen retorna imagen mockeada
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(detalleMock.getImagen()).thenReturn(null);
    // ejecuto el modelo
    ModelAndView modelAndView = controladorSubasta.verDetalle(1L, requestMock);
    // verifico haber obtenido en el modelo clave ImagenBase64 y es un texto
    assertThat(modelAndView.getModel().get("imagenBase64"), nullValue());
  }

  @Test
  public void verDetalleDeSubastaCerradaDeberiaAgregarGanadorYMontoGanadorAlModelo() {
    Usuario ganador = new Usuario();
    ganador.setEmail("test@unlam.edu.ar");

    Oferta ofertaGanadora = new Oferta();
    ofertaGanadora.setUsuario(ganador);
    ofertaGanadora.setMonto(2500.0);

    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(subastaMock.getId()).thenReturn(1L);
    when(subastaMock.getEstadoSubasta()).thenReturn(EstadoSubasta.CERRADA);
    when(servicioOfertaMock.obtenerMejorOfertaPorSubasta(1L)).thenReturn(ofertaGanadora);

    ModelAndView modelAndView = controladorSubasta.verDetalle(1L, requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("detalle-subasta"));
    assertThat(modelAndView.getModel().get("ganador"), equalTo(ganador));
    assertThat(modelAndView.getModel().get("montoGanador"), equalTo(2500.0));
  }

  @Test
  public void verDetallesinSesionDeberiaVerElDetalleDelaSubasta() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    HttpSession sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null); // sin sesion

    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);

    ModelAndView mav = controladorSubasta.verDetalle(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("detalle-subasta"));
    assertThat(mav.getModel().get("esCreador"), equalTo(false));
  }

  @Test
  public void verDetalleConSesionYEsCreadorDeberiaAgregarEsCreadorTrueAlModelo() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    HttpSession sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L); // usuario id=1

    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(subastaMock.esCreador(any())).thenReturn(true); // es el creador

    ModelAndView mav = controladorSubasta.verDetalle(1L, requestMock);

    assertThat(mav.getModel().get("esCreador"), equalTo(true));
  }

  @Test
  public void verDetalleConSesionYNoEsCreadorDeberiaAgregarEsCreadorFalseAlModelo() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    HttpSession sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(2L); // usuario id=2

    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(subastaMock.esCreador(any())).thenReturn(false); // no es el creador

    ModelAndView mav = controladorSubasta.verDetalle(1L, requestMock);

    assertThat(mav.getModel().get("esCreador"), equalTo(false));
  }

  @Test
  public void listarSubastaConSesionDeberiaRetornarVistaSubastasConSesionActiva() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(servicioSubastaMock.obtenerTodasLasSubastas()).thenReturn(List.of());
    ModelAndView modelAndView = controladorSubasta.listarSubastas(requestMock, null);
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("subastas"));
    assertThat(modelAndView.getModel().get("estaLogueado"), equalTo(true));
  }

  @Test
  public void listarSubastaSinSesionDeberiaRetornarVistaSubastasSinSesionActiva() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);
    when(servicioSubastaMock.obtenerTodasLasSubastas()).thenReturn(List.of());
    ModelAndView modelAndView = controladorSubasta.listarSubastas(requestMock, null);
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("subastas"));
    assertThat(modelAndView.getModel().get("estaLogueado"), equalTo(false));
  }

  @Test
  public void listarSubastasSinBusquedaDeberiaMostrarTodasLasSubastas() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(servicioSubastaMock.obtenerSubastas(null)).thenReturn(List.of());

    ModelAndView modelAndView = controladorSubasta.listarSubastas(requestMock, null);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("subastas"));
    assertThat(modelAndView.getModel().get("subastas"), equalTo(List.of()));
    verify(servicioSubastaMock).obtenerSubastas(null);
  }

  @Test
  public void listarSubastasConBusquedaDeberiaBuscarSubasta() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(servicioSubastaMock.obtenerSubastas("silla")).thenReturn(List.of());

    ModelAndView modelAndView = controladorSubasta.listarSubastas(requestMock, "silla");

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("subastas"));
    assertThat(modelAndView.getModel().get("busqueda"), equalTo("silla"));
    verify(servicioSubastaMock).obtenerSubastas("silla");
  }

  @Test
  public void listarSubastaConBusquedaVaciaDeberiaRedirigirASubastas() {
    ModelAndView modelAndView = controladorSubasta.listarSubastas(requestMock, "");

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/subastas"));
    verify(servicioSubastaMock, never()).obtenerSubastas(any());
  }

  @Test
  public void irAEditarSubastaComoCreadorDeberiaRetornarVistaEditar() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(subastaMock.esCreador(any())).thenReturn(true);

    ModelAndView mav = controladorSubasta.irAEditarSubasta(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("editar-subasta"));
    assertThat(mav.getModel().get("subasta"), instanceOf(Subasta.class));
  }

  @Test
  public void irAEditarSubastaComoNoCreadorDeberiaRedirigirADetalle() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(2L);
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(subastaMock.esCreador(any())).thenReturn(false);

    ModelAndView mav = controladorSubasta.irAEditarSubasta(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/detalle-subasta?id=1"));
  }

  @Test
  public void editarSubastaConDatosValidosDeberiaRedirigirADetalle() throws Exception {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);

    ModelAndView mav = controladorSubasta.editarSubasta(
      1L,
      "Mouse",
      "desc",
      "Perifericos",
      imagenMock,
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/detalle-subasta?id=1"));
    verify(servicioSubastaMock, times(1))
      .editarSubasta(eq(1L), eq("Mouse"), eq("desc"), eq("Perifericos"), eq(imagenMock), any());
  }

  @Test
  public void editarSubastaComoNoCreadorDeberiaVolverAlFormularioConError() throws Exception {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(2L);
    doThrow(SubastaNoEditableException.class)
      .when(servicioSubastaMock)
      .editarSubasta(anyLong(), any(), any(), any(), any(), any());

    ModelAndView mav = controladorSubasta.editarSubasta(
      1L,
      "Mouse",
      "desc",
      "Perifericos",
      imagenMock,
      requestMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("editar-subasta"));
    assertThat(
      mav.getModel().get("error").toString(),
      equalToIgnoringCase("No tenés permiso para editar esta subasta")
    );
  }
}
