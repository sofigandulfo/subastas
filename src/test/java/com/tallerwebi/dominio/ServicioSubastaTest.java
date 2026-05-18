package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioSubastaTest {

  private ServicioSubasta servicioSubasta;
  private RepositorioSubasta repositorioSubasta;

  @BeforeEach
  public void init() {
    repositorioSubasta = mock(RepositorioSubasta.class);
    servicioSubasta = new ServicioSubastaImpl(repositorioSubasta);
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

    servicioSubasta.crearSubasta(subasta, null);

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

    servicioSubasta.crearSubasta(subasta, null);

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

    servicioSubasta.crearSubasta(subasta, null);

    EstadoSubasta resultadoEsperado = EstadoSubasta.ACTIVA;
    EstadoSubasta resultadoObtenido = subasta.getEstadoSubasta();
    assertEquals(resultadoEsperado, resultadoObtenido);
  }

  @Test
  public void queNoSePuedaCrearUnaSubastaSinNombre() {
    Subasta subasta = new Subasta("", "Notebook 16gb", 1000.0, 5000.0, "Tecnologia", "nuevo");

    assertThrows(SubastaInvalidaExeption.class, () -> servicioSubasta.crearSubasta(subasta, null));
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

    assertThrows(SubastaInvalidaExeption.class, () -> servicioSubasta.crearSubasta(subasta, null));
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

    assertThrows(SubastaInvalidaExeption.class, () -> servicioSubasta.crearSubasta(subasta, null));
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

    assertThrows(SubastaInvalidaExeption.class, () -> servicioSubasta.crearSubasta(subasta, null));
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
}
