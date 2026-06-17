package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.SubastaConOfertasException;
import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import com.tallerwebi.dominio.excepcion.SubastaNoEditableException;
import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.oferta.RepositorioOferta;
import com.tallerwebi.dominio.subasta.EstadoSubasta;
import com.tallerwebi.dominio.subasta.RepositorioSubasta;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.subasta.ServicioSubastaImpl;
import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.dominio.usuario.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

public class ServicioSubastaTest {

  private ServicioSubasta servicioSubasta;
  private RepositorioSubasta repositorioSubasta;
  private RepositorioOferta repositorioOferta;
  private Usuario usuarioCreador;

  @BeforeEach
  public void init() {
    repositorioSubasta = mock(RepositorioSubasta.class);
    repositorioOferta = mock(RepositorioOferta.class);
    servicioSubasta = new ServicioSubastaImpl(repositorioSubasta, repositorioOferta);
    usuarioCreador = new Usuario();
    usuarioCreador.setId(1L);
  }

  @Test
  public void cuandoUnUsuarioCreaUnaSubastaSeLoAsignaComoUsuarioCreadorCorrectamente()
    throws SubastaInvalidaExeption {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setFechaCierre(LocalDateTime.now().plusDays(1));
    servicioSubasta.crearSubasta(subasta, null, usuarioCreador);
    assertSame(usuarioCreador, subasta.getCreador());
  }

  @Test
  public void cuandoUnUsuarioCreaUnaSubastaSeLoReconoceComoCreador() {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setCreador(usuarioCreador);
    assertTrue(subasta.esCreador(usuarioCreador));
  }

  @Test
  public void cuandoPreguntoSiUnUsuarioQueNoCreoLaSubastaEsCreadorDevuelveFalse() {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setCreador(usuarioCreador);
    Usuario otroUsuario = new Usuario();
    otroUsuario.setId(2L);
    assertFalse(subasta.esCreador(otroUsuario));
  }

  @Test
  public void cuandoPreguntoSiUnUsuarioSinIdEsCreadorDevuelveFalse() {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setCreador(usuarioCreador);
    Usuario usuarioSinId = new Usuario();
    assertFalse(subasta.esCreador(usuarioSinId));
  }

  @Test
  public void queUnaSubastaTengaLosAtributosCorrectos() throws SubastaInvalidaExeption {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().plusDays(1));
    servicioSubasta.crearSubasta(subasta, null, usuarioCreador);
    assertEquals("Notebook", subasta.getDetalle().getNombre());
    assertEquals("Notebook 16gb", subasta.getDetalle().getDescripcion());
    assertEquals(1000.0, subasta.getPrecioInicial());
    assertEquals(5000.0, subasta.getPrecioMaximo());
    assertEquals("Tecnologia", subasta.getDetalle().getCategoria());
    assertEquals("nuevo", subasta.getDetalle().getEstadoArticulo());
  }

  @Test
  public void queSeActualicenLosAtributosDeUnaSubasta() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.getDetalle().setNombre("Mouse");
    subasta.getDetalle().setDescripcion("Mouse inalambrico");
    subasta.setPrecioInicial(500.0);
    subasta.setPrecioMaximo(2000.0);
    subasta.getDetalle().setCategoria("Perifericos");
    subasta.getDetalle().setEstadoArticulo("usado");
    assertEquals("Mouse", subasta.getDetalle().getNombre());
    assertEquals("Mouse inalambrico", subasta.getDetalle().getDescripcion());
    assertEquals(500.0, subasta.getPrecioInicial());
    assertEquals(2000.0, subasta.getPrecioMaximo());
    assertEquals("Perifericos", subasta.getDetalle().getCategoria());
    assertEquals("usado", subasta.getDetalle().getEstadoArticulo());
  }

  @Test
  public void queAlCrearUnaSubastaValidaSeGuardaEnLaBaseDeDatosCorrectamente()
    throws SubastaInvalidaExeption {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().plusDays(1));
    servicioSubasta.crearSubasta(subasta, null, usuarioCreador);
    verify(this.repositorioSubasta, times(1)).guardarSubasta(subasta);
  }

  @Test
  public void queAlCrearUnaSubastaValidaQuedaComoActiva() throws SubastaInvalidaExeption {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().plusDays(1));
    servicioSubasta.crearSubasta(subasta, null, usuarioCreador);
    assertEquals(EstadoSubasta.ACTIVA, subasta.getEstadoSubasta());
  }

  @Test
  public void queNoSePuedaCrearUnaSubastaSinNombre() {
    Subasta subasta = new Subasta("", "Notebook 16gb", 1000.0, 5000.0, "Tecnologia", "nuevo");
    assertThrows(
      SubastaInvalidaExeption.class,
      () -> servicioSubasta.crearSubasta(subasta, null, usuarioCreador)
    );
  }

  @Test
  public void queNoSePuedaCrearUnaSubastaConPrecioInicialNegativo() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      -1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    assertThrows(
      SubastaInvalidaExeption.class,
      () -> servicioSubasta.crearSubasta(subasta, null, usuarioCreador)
    );
  }

  @Test
  public void queNoSePuedaCrearUnaSubastaConPrecioMaximoNegativo() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      -5000.0,
      "Tecnologia",
      "nuevo"
    );
    assertThrows(
      SubastaInvalidaExeption.class,
      () -> servicioSubasta.crearSubasta(subasta, null, usuarioCreador)
    );
  }

  @Test
  public void queNoSePuedaCrearUnaSubastaConPrecioMaximoMenosAlPrecioInicial() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      100.0,
      "Tecnologia",
      "nuevo"
    );
    assertThrows(
      SubastaInvalidaExeption.class,
      () -> servicioSubasta.crearSubasta(subasta, null, usuarioCreador)
    );
  }

  @Test
  public void dadoQueLaSubastaSeVaAIrPujandoDeberiaDeTenerUnPrecioActualQueInicializadoDeberiaSerIgualAlPrecioInicial() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    assertEquals(1000.0, subasta.getPrecioActual());
  }

  @Test
  public void dadoQueAlguienPujoLaOfertaElPrecioActualDeberiaSerActualizado() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(1100.0);
    assertEquals(1100.0, subasta.getPrecioActual());
  }

  @Test
  public void queAlIgualarElPrecioMaximoLaSubastaPasaAEstadoCuentaAtras() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(3000.0);
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);
    servicioSubasta.verificarPrecioMaximo(1L);
    assertEquals(EstadoSubasta.CUENTA_ATRAS, subasta.getEstadoSubasta());
  }

  @Test
  public void queAlIgualarElPrecioMaximoLaFechaDeCierreSeSetiaADosHorasDesdeAhora() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(3000.0);
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);
    LocalDateTime antes = LocalDateTime.now();
    servicioSubasta.verificarPrecioMaximo(1L);
    LocalDateTime despues = LocalDateTime.now();
    assertThat(subasta.getFechaCierre(), greaterThanOrEqualTo(antes.plusHours(2)));
    assertThat(subasta.getFechaCierre(), lessThanOrEqualTo(despues.plusHours(2)));
  }

  @Test
  public void queAlNoIgualarElPrecioMaximoLaSubastaNoDeberiaActualizarEstado() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(2000.0);
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);
    servicioSubasta.verificarPrecioMaximo(1L);
    assertEquals(EstadoSubasta.ACTIVA, subasta.getEstadoSubasta());
    assertNull(subasta.getFechaCierre());
  }

  @Test
  public void queAlSuperarElPrecioMaximoLaSubastaPasaAEstadoCuentaAtras() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(4000.0);
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);
    servicioSubasta.verificarPrecioMaximo(1L);
    assertEquals(EstadoSubasta.CUENTA_ATRAS, subasta.getEstadoSubasta());
  }

  @Test
  public void queAlIgualarElPrecioMaximoSeGuardaLaSubastaEnElRepositorio() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(3000.0);
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);
    servicioSubasta.verificarPrecioMaximo(1L);
    verify(repositorioSubasta, times(1)).guardarSubasta(subasta);
  }

  @Test
  public void queAlVencerLaFechaDeCierreLaSubastaPasaAEstadoCerrada() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setEstadoSubasta(EstadoSubasta.CUENTA_ATRAS);
    subasta.setFechaCierre(LocalDateTime.now().minusHours(1));
    when(repositorioSubasta.obtenerSubastasPorVencer()).thenReturn(List.of(subasta));
    servicioSubasta.cerrarSubastasPorTiempo();
    assertEquals(EstadoSubasta.CERRADA, subasta.getEstadoSubasta());
  }

  @Test
  public void queAlCerrarLaSubastaSeArmaElPodioConLosTresMejoresOfertadores() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().minusHours(1));

    Usuario usuario1 = new Usuario();
    usuario1.setId(1L);
    Usuario usuario2 = new Usuario();
    usuario2.setId(2L);
    Usuario usuario3 = new Usuario();
    usuario3.setId(3L);

    // El repositorio ahora devuelve TODAS las ofertas ordenadas por monto DESC
    // (puede haber varias del mismo usuario); el servicio filtra la mejor por usuario
    Oferta oferta1 = new Oferta(3000.0, subasta, usuario1);
    Oferta oferta1b = new Oferta(2500.0, subasta, usuario1); // segunda oferta del mismo usuario
    Oferta oferta2 = new Oferta(2000.0, subasta, usuario2);
    Oferta oferta3 = new Oferta(1500.0, subasta, usuario3);

    when(repositorioSubasta.obtenerSubastasPorVencer()).thenReturn(List.of(subasta));
    when(repositorioOferta.obtenerMejoresOfertasPorSubasta(subasta.getId()))
      .thenReturn(List.of(oferta1, oferta1b, oferta2, oferta3));

    servicioSubasta.cerrarSubastasPorTiempo();

    // el podio tiene 3 usuarios distintos, sin repetir
    assertEquals(3, subasta.getPodio().size());
    assertEquals(usuario1, subasta.getPodio().get(0));
    assertEquals(usuario2, subasta.getPodio().get(1));
    assertEquals(usuario3, subasta.getPodio().get(2));
  }

  @Test
  public void queAlCerrarLaSubastaConMenosDeTresOfertasElPodioTengaSoloLasQueHay() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().minusHours(1));

    Usuario usuario1 = new Usuario();
    usuario1.setId(1L);
    Usuario usuario2 = new Usuario();
    usuario2.setId(2L);

    Oferta oferta1 = new Oferta(3000.0, subasta, usuario1);
    Oferta oferta2 = new Oferta(2000.0, subasta, usuario2);

    when(repositorioSubasta.obtenerSubastasPorVencer()).thenReturn(List.of(subasta));
    when(repositorioOferta.obtenerMejoresOfertasPorSubasta(subasta.getId()))
      .thenReturn(List.of(oferta1, oferta2));

    servicioSubasta.cerrarSubastasPorTiempo();

    assertEquals(2, subasta.getPodio().size());
    assertEquals(usuario1, subasta.getPodio().get(0));
    assertEquals(usuario2, subasta.getPodio().get(1));
  }

  @Test
  public void queAlCerrarLaSubastaSinOfertasElPodioQuedeVacio() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().minusHours(1));
    when(repositorioSubasta.obtenerSubastasPorVencer()).thenReturn(List.of(subasta));
    when(repositorioOferta.obtenerMejoresOfertasPorSubasta(subasta.getId())).thenReturn(List.of());
    servicioSubasta.cerrarSubastasPorTiempo();
    assertEquals(0, subasta.getPodio().size());
  }

  @Test
  public void queAlCerrarLaSubastaSeGuardaEnElRepositorio() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().minusHours(1));
    when(repositorioSubasta.obtenerSubastasPorVencer()).thenReturn(List.of(subasta));
    when(repositorioOferta.obtenerMejoresOfertasPorSubasta(subasta.getId())).thenReturn(List.of());
    servicioSubasta.cerrarSubastasPorTiempo();
    verify(repositorioSubasta, times(1)).guardarSubasta(subasta);
  }

  @Test
  public void queNoSePuedaCrearUnaSubastaSinFechaDeCierre() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    assertThrows(
      SubastaInvalidaExeption.class,
      () -> servicioSubasta.crearSubasta(subasta, null, usuarioCreador)
    );
  }

  @Test
  public void queNoSePuedaCrearUnaSubastaConFechaDeCierrePasada() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().minusDays(1));
    assertThrows(
      SubastaInvalidaExeption.class,
      () -> servicioSubasta.crearSubasta(subasta, null, usuarioCreador)
    );
  }

  @Test
  public void queNoSePuedaCrearUnaSubastaSiLaImagenSuperaElTamanioMaximo() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().plusDays(1));

    MultipartFile imagenGrandeMock = mock(MultipartFile.class);
    when(imagenGrandeMock.isEmpty()).thenReturn(false);

    // si el archivo supera el limite (6mb por ejemplo)
    when(imagenGrandeMock.getSize()).thenReturn(6 * 1024 * 1024L);
    assertThrows(
      SubastaInvalidaExeption.class,
      () -> {
        servicioSubasta.crearSubasta(subasta, imagenGrandeMock, usuarioCreador);
      }
    );

    verify(this.repositorioSubasta, never()).guardarSubasta(any(Subasta.class));
  }

  @Test
  public void queElCreadorPuedeEliminarSuSubasta() throws Exception {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setCreador(usuarioCreador);
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    servicioSubasta.eliminarSubasta(1L, usuarioCreador);

    verify(repositorioSubasta, times(1)).eliminarSubasta(subasta);
  }

  @Test
  public void queUnUsuarioQueNoEsElCreadorNoPuedeEliminarLaSubasta() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setCreador(usuarioCreador);

    Usuario otroUsuario = new Usuario();
    otroUsuario.setId(2L);

    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(Exception.class, () -> servicioSubasta.eliminarSubasta(1L, otroUsuario));

    verify(repositorioSubasta, never()).eliminarSubasta(subasta);
  }

  @Test
  public void queNoSePuedaEliminarUnaSubastaQueNoExiste() {
    when(repositorioSubasta.obtenerSubasta(99L)).thenReturn(null);

    assertThrows(Exception.class, () -> servicioSubasta.eliminarSubasta(99L, usuarioCreador));

    verify(repositorioSubasta, never()).eliminarSubasta(any());
  }

  @Test
  public void queNoSePuedaEliminarUnaSubastaQueYaTieneOfertas() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setCreador(usuarioCreador);

    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);
    when(repositorioOferta.obtenerMejorOfertaPorSubasta(1L)).thenReturn(new Oferta());

    assertThrows(
      SubastaConOfertasException.class,
      () -> servicioSubasta.eliminarSubasta(1L, usuarioCreador)
    );

    verify(repositorioSubasta, never()).eliminarSubasta(subasta);
  }

  @Test
  public void queElCreadorPuedeEditarLosCamposDelDetalle() throws SubastaNoEditableException {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setId(1L);
    subasta.setCreador(usuarioCreador);

    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    servicioSubasta.editarSubasta(
      1L,
      "Mouse",
      "Mouse inalambrico",
      "Perifericos",
      null,
      usuarioCreador
    );

    assertEquals("Mouse", subasta.getDetalle().getNombre());
    assertEquals("Mouse inalambrico", subasta.getDetalle().getDescripcion());
    assertEquals("Perifericos", subasta.getDetalle().getCategoria());
  }

  @Test
  public void queAlEditarSeGuardaEnElRepositorio() throws SubastaNoEditableException {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setId(1L);
    subasta.setCreador(usuarioCreador);

    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    servicioSubasta.editarSubasta(
      1L,
      "Mouse",
      "Mouse inalambrico",
      "Perifericos",
      null,
      usuarioCreador
    );

    verify(repositorioSubasta, times(1)).guardarSubasta(subasta);
  }

  @Test
  public void queUnUsuarioQueNoEsCreadorNoPuedeEditar() {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setId(1L);
    subasta.setCreador(usuarioCreador);

    Usuario otroUsuario = new Usuario();
    otroUsuario.setId(2L);

    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(
      SubastaNoEditableException.class,
      () -> servicioSubasta.editarSubasta(1L, "Mouse", "desc", "Perifericos", null, otroUsuario)
    );
  }

  @Test
  public void queAlEditarNoCambianLosPrecios() throws SubastaNoEditableException {
    Subasta subasta = new Subasta("Notebook", "desc", 1000.0, 5000.0, "Tecnologia", "nuevo");
    subasta.setId(1L);
    subasta.setCreador(usuarioCreador);

    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    servicioSubasta.editarSubasta(1L, "Mouse", "desc", "Perifericos", null, usuarioCreador);

    assertEquals(1000.0, subasta.getPrecioInicial());
    assertEquals(5000.0, subasta.getPrecioMaximo());
  }

  @Test
  public void queVerificarPrecioMaximoNoHagaNadaSiLaSubastaYaEstaCerrada() {
    // preparacion
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(3000.0);
    subasta.setEstadoSubasta(EstadoSubasta.CERRADA);
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    // ejecucion
    servicioSubasta.verificarPrecioMaximo(1L);

    // validacion
    assertEquals(EstadoSubasta.CERRADA, subasta.getEstadoSubasta());
    verify(repositorioSubasta, never()).guardarSubasta(subasta);
  }

  @Test
  public void queCerrarSubastasPorTiempoNoHagaNadaSiNoHaySubastasPorVencer() {
    // preparacion
    when(repositorioSubasta.obtenerSubastasPorVencer()).thenReturn(List.of());

    // ejecucion
    servicioSubasta.cerrarSubastasPorTiempo();

    // validacion
    verify(repositorioSubasta, never()).guardarSubasta(any());
    verify(repositorioOferta, never()).obtenerMejoresOfertasPorSubasta(any());
  }
}
