package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    EstadoSubasta resultadoEsperado = EstadoSubasta.ACTIVA;
    EstadoSubasta resultadoObtenido = subasta.getEstadoSubasta();
    assertEquals(resultadoEsperado, resultadoObtenido);
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
    subasta.setPrecioActual(3000.0); // igualamos el precio maximo
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    servicioSubasta.verificarPrecioMaximo(1L);

    assertEquals(EstadoSubasta.CUENTA_ATRAS, subasta.getEstadoSubasta());
  }

  @Test
  public void queAlIgualarElPrecioMaximoLaFechaDeCierreSeSetiaADosHorasDesdeAhora() {
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
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    LocalDateTime antes = LocalDateTime.now();

    // ejecucion
    servicioSubasta.verificarPrecioMaximo(1L);

    LocalDateTime despues = LocalDateTime.now();

    // validacion
    assertThat(subasta.getFechaCierre(), greaterThanOrEqualTo(antes.plusHours(2)));
    assertThat(subasta.getFechaCierre(), lessThanOrEqualTo(despues.plusHours(2)));
  }

  @Test
  public void queAlNoIgualarElPrecioMaximoLaSubastaNoDeberiaActualizarEstado() {
    // preparacion
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(2000.0); // menor al precio maximo
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    // ejecucion
    servicioSubasta.verificarPrecioMaximo(1L);

    // validacion
    assertEquals(EstadoSubasta.ACTIVA, subasta.getEstadoSubasta());
    assertNull(subasta.getFechaCierre());
  }

  @Test
  public void queAlSuperarElPrecioMaximoLaSubastaPasaAEstadoCuentaAtras() {
    // preparacion
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setPrecioActual(4000.0); // supera el precio maximo
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    // ejecucion
    servicioSubasta.verificarPrecioMaximo(1L);

    // validacion
    assertEquals(EstadoSubasta.CUENTA_ATRAS, subasta.getEstadoSubasta());
  }

  @Test
  public void queAlIgualarElPrecioMaximoSeGuardaLaSubastaEnElRepositorio() {
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
    when(repositorioSubasta.obtenerSubasta(1L)).thenReturn(subasta);

    // ejecucion
    servicioSubasta.verificarPrecioMaximo(1L);

    // validacion
    verify(repositorioSubasta, times(1)).guardarSubasta(subasta);
  }

  @Test
  public void queAlVencerLaFechaDeCierreLaSubastaPasaAEstadoCerrada() {
    // preparacion
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      3000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setEstadoSubasta(EstadoSubasta.CUENTA_ATRAS);
    subasta.setFechaCierre(LocalDateTime.now().minusHours(1)); // fecha ya vencida
    when(repositorioSubasta.obtenerSubastasPorVencer()).thenReturn(List.of(subasta));

    // ejecucion
    servicioSubasta.cerrarSubastasPorTiempo();

    // validacion
    assertEquals(EstadoSubasta.CERRADA, subasta.getEstadoSubasta());
  }

  @Test
  public void queAlCerrarLaSubastaSeArmaElPodioConLosTresMejoresOfertadores() {
    // preparacion
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

    Oferta oferta1 = new Oferta(3000.0, subasta, usuario1);
    Oferta oferta2 = new Oferta(2000.0, subasta, usuario2);
    Oferta oferta3 = new Oferta(1500.0, subasta, usuario3);

    when(repositorioSubasta.obtenerSubastasPorVencer()).thenReturn(List.of(subasta));
    when(repositorioOferta.obtenerMejoresOfertasPorSubasta(subasta.getId()))
      .thenReturn(List.of(oferta1, oferta2, oferta3));

    // ejecucion
    servicioSubasta.cerrarSubastasPorTiempo();

    // validacion
    assertEquals(usuario1, subasta.getPodio().get(0));
    assertEquals(usuario2, subasta.getPodio().get(1));
    assertEquals(usuario3, subasta.getPodio().get(2));
  }

  @Test
  public void queAlCerrarLaSubastaConMenosDeTresOfertasElPodioTengaSoloLasQueHay() {
    // preparacion
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

    // ejecucion
    servicioSubasta.cerrarSubastasPorTiempo();

    // validacion
    assertEquals(2, subasta.getPodio().size());
    assertEquals(usuario1, subasta.getPodio().get(0));
    assertEquals(usuario2, subasta.getPodio().get(1));
  }

  @Test
  public void queAlCerrarLaSubastaSinOfertasElPodioQuedeVacio() {
    // preparacion
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

    // ejecucion
    servicioSubasta.cerrarSubastasPorTiempo();

    // validacion
    assertEquals(0, subasta.getPodio().size());
  }

  @Test
  public void queAlCerrarLaSubastaSeGuardaEnElRepositorio() {
    // preparacion
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

    // ejecucion
    servicioSubasta.cerrarSubastasPorTiempo();

    // validacion
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
      () -> {
        servicioSubasta.crearSubasta(subasta, null, usuarioCreador);
      }
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
      () -> {
        servicioSubasta.crearSubasta(subasta, null, usuarioCreador);
      }
    );
  }
}
