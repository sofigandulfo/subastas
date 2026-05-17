package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.DetalleSubasta;
import com.tallerwebi.dominio.ServicioSubasta;
import com.tallerwebi.dominio.Subasta;
import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
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

  @BeforeEach
  public void init() {
    subastaDTO = mock(SubastaDTO.class);
    subastaMock = mock(Subasta.class);
    detalleMock = mock(DetalleSubasta.class);
    when(subastaMock.getDetalle()).thenReturn(detalleMock);
    servicioSubastaMock = mock(ServicioSubasta.class);
    controladorSubasta = new ControladorSubasta(servicioSubastaMock);
    imagenMock = mock(MultipartFile.class);
  }

  @Test
  public void irACrearSubastaDeberiaRetornarVistaCrearSubastaConSubastaVacia() {
    ModelAndView modelAndView = controladorSubasta.irAlFormulario();
    // verifico que la vista es la corresta: cuando el usuario ingresa
    // /crear-subasta
    // se encuentra en el form para crear la subasta?
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    // verifico que haya un objeto subasta vacío en el modelo para cargarle datos
    assertThat(modelAndView.getModel().get("subasta"), instanceOf(SubastaDTO.class));
  }

  @Test
  public void crearSubastaConDatosValidosDeberiaRedirigirAVistaDetalleSubasta()
    throws SubastaInvalidaExeption {
    when(subastaDTO.entidad()).thenReturn(subastaMock);
    when(servicioSubastaMock.crearSubasta(subastaMock, imagenMock)).thenReturn(subastaMock);
    when(subastaMock.getId()).thenReturn(1L);

    ModelAndView modelAndView = controladorSubasta.crearSubasta(subastaDTO, imagenMock);
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/detalle-subasta?id=1"));
    verify(servicioSubastaMock, times(1)).crearSubasta(subastaMock, imagenMock);
  }

  @Test
  public void crearSubastaConDatosInvalidosDeberiaVolverAlFormularioConError()
    throws SubastaInvalidaExeption {
    when(subastaDTO.entidad()).thenReturn(subastaMock);
    // preparacion: simulo que el servicio lanza una exception
    doThrow(SubastaInvalidaExeption.class)
      .when(servicioSubastaMock)
      .crearSubasta(subastaMock, imagenMock);
    // ejecucion
    ModelAndView modelAndView = controladorSubasta.crearSubasta(subastaDTO, imagenMock);
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
    doThrow(RuntimeException.class).when(servicioSubastaMock).crearSubasta(subastaMock, imagenMock);
    // ejercucion
    ModelAndView modelAndView = controladorSubasta.crearSubasta(subastaDTO, imagenMock);
    // valido
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Error al registrar nueva subasta")
    );
  }

  @Test
  public void verDetalleConIdValidoDeberiaRetornarVistaDetalleConSubasta() {
    //preparacion->No me importa la logica del SERVICIO por lo que mockeo
    //lo que debe devolver cuando le pida la subasta con id 1.
    //Simulo: "existe una subasta con id 1 en la BD"
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock); //cuando le pido al servicio una subasta con id 1L me retorna una subasta falsa.
    //ejecucion ->Llamo al CONTROLADOR
    ModelAndView modelAndView = controladorSubasta.verDetalle(1L); //mostrame la vista y el modelo de subasta id 1L
    //valido-> Verifico que el controlador devuelva la vista y un objeto de tipo subasta en el modelo
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("detalle-subasta"));
    assertThat(modelAndView.getModel().get("subasta"), instanceOf(Subasta.class));
  }

  @Test
  public void verDetalleConIdInexistenteDeberiaVolverAlFormularioConError() {
    //preparacion-> El servicio devuelve null al llamar un id por ejemplo 200L
    when(servicioSubastaMock.obtenerSubasta(200L)).thenReturn(null);
    //ejecucion
    ModelAndView modelAndView = controladorSubasta.verDetalle(200L);
    //verifico que no paso a la vista de detalle con una subasta y me haya llevado al formulario con error
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Subasta no encontrada")
    );
  }

  //test para imagen dinamica:

  @Test
  public void verDetalleConSubastaQuetieneImagenDeberiaAgregarImagenBase64AlModeloEnFormaDeTexto() {
    //PREPARACION cuando llame al servicio con long 1 retorna la subasta mockeada
    //            cuando llame a la subasta con su imagen retorna imagen mockeada
    byte[] imagenBytes = new byte[] { 1, 2, 3 };
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(detalleMock.getImagen()).thenReturn(imagenBytes);
    //ejecuto el modelo
    ModelAndView modelAndView = controladorSubasta.verDetalle(1L);
    //verifico haber obtenido en el modelo clave ImagenBase64 y es un texto
    assertThat(modelAndView.getModel().get("imagenBase64"), instanceOf((String.class)));
  }

  @Test
  public void verDetalleConSubastaSinImagenNoDeberiaAgregarImagenBase64AlModelo() {
    //PREPARACION cuando llame al servicio con long 1 retorna la subasta mockeada
    //            cuando llame a la subasta con su imagen retorna imagen mockeada
    when(servicioSubastaMock.obtenerSubasta(1L)).thenReturn(subastaMock);
    when(detalleMock.getImagen()).thenReturn(null);
    //ejecuto el modelo
    ModelAndView modelAndView = controladorSubasta.verDetalle(1L);
    //verifico haber obtenido en el modelo clave ImagenBase64 y es un texto
    assertThat(modelAndView.getModel().get("imagenBase64"), nullValue());
  }
}
