package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioOfertaTest {

  private RepositorioSubasta repositorioSubastaMock;
  private RepositorioOferta repositorioOfertaMock;
  private ServicioOferta servicioOferta;
  private ServicioSubasta servicioSubastaMock;
  private RepositorioUsuario repositorioUsuarioMock;
  private ServicioAutoPuja servicioAutoPujaMock;
  private Usuario ofertantePrueba;

  @BeforeEach
  public void init() {
    repositorioSubastaMock = mock(RepositorioSubasta.class);
    repositorioOfertaMock = mock(RepositorioOferta.class);
    servicioSubastaMock = mock(ServicioSubasta.class);
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    servicioAutoPujaMock = mock(ServicioAutoPuja.class);
    // Le inyectamos ambos mocks al constructor del servicio
    servicioOferta =
      new ServicioOfertaImpl(
        repositorioSubastaMock,
        repositorioOfertaMock,
        servicioSubastaMock,
        repositorioUsuarioMock,
        servicioAutoPujaMock
      );

    ofertantePrueba = new Usuario();
    ofertantePrueba.setId(1L);
  }

  @Test
  public void queSePuedaCrearUnaOferta() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    Oferta nuevaOferta = new Oferta(1100.0, subasta);

    assertEquals(1100.0, nuevaOferta.getMonto());
    assertEquals(subasta, nuevaOferta.getSubasta());
  }

  @Test
  public void queSePuedaModificarElMontoDeUnaOferta() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    Oferta nuevaOferta = new Oferta(1100.0, subasta);
    nuevaOferta.setMonto(1200.0);
    assertEquals(1200.0, nuevaOferta.getMonto());
  }

  @Test
  void cuandoSeSeIngresaUnaOfertaMayorAlPrecioActualSeDeberiaActualizarElPrecioDeLaSubasta()
    throws OfertaInvalidaException, SubastaNoEncontradaException {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    Oferta nuevaOferta = new Oferta(1100.0, subasta);

    // Le enseñamos al mock qué responder cuando el servicio intente buscar la subasta
    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    servicioOferta.procesarOferta(1L, nuevaOferta, ofertantePrueba);

    // Validamos que el precio actual de la subasta se actualizó
    assertEquals(1100.0, subasta.getPrecioActual());

    assertEquals(ofertantePrueba, nuevaOferta.getUsuario());

    // Ademas validamos que el servicio haya llamado al metodo guardarOferta
    verify(repositorioOfertaMock, times(1)).guardarOferta(nuevaOferta);
    verify(servicioAutoPujaMock, times(1)).procesarAutoPujas(subasta, nuevaOferta);
    verify(servicioSubastaMock, times(1)).verificarPrecioMaximo(1L);
  }

  @Test
  void alIntentarHacerUnaOfertaMenorOIgualAlPrecioActualDeberiaTirarUnaExcepcion() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    Oferta nuevaOferta = new Oferta(900.0, subasta);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(
      OfertaInvalidaException.class,
      () -> {
        servicioOferta.procesarOferta(1L, nuevaOferta, ofertantePrueba);
      }
    );
    verify(servicioSubastaMock, never()).verificarPrecioMaximo(1L);
    verify(servicioAutoPujaMock, never()).procesarAutoPujas(any(Subasta.class), any(Oferta.class));
  }

  @Test
  void alIntentarHacerUnaOfertaDeUnObjetoDeUnaSubastaConNombreVacioDeberiaTirarUnaExcepcion() {
    Subasta subasta = new Subasta("", "Notebook 16gb", 1000.0, 5000.0, "Tecnologia", "nuevo");
    Oferta nuevaOferta = new Oferta(1100.0, subasta);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(
      SubastaNoEncontradaException.class,
      () -> {
        servicioOferta.procesarOferta(1L, nuevaOferta, ofertantePrueba);
      }
    );
    verify(servicioSubastaMock, never()).verificarPrecioMaximo(1L);
    verify(servicioAutoPujaMock, never()).procesarAutoPujas(any(Subasta.class), any(Oferta.class));
  }

  @Test
  void alIntenarOfertarEnSubastaCerradaDeberiaTirarExcepcion() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setEstadoSubasta(EstadoSubasta.CERRADA);

    Oferta nuevaOferta = new Oferta(1100.0, subasta);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(
      OfertaInvalidaException.class,
      () -> {
        servicioOferta.procesarOferta(1L, nuevaOferta, ofertantePrueba);
      }
    );

    verify(repositorioOfertaMock, never()).guardarOferta(nuevaOferta);
    verify(servicioAutoPujaMock, never()).procesarAutoPujas(any(Subasta.class), any(Oferta.class));
  }

  @Test
  void alIntentarOfertarEnSubastaVencidaDeberiaTirarExcepcion() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setFechaCierre(LocalDateTime.now().minusHours(1));

    Oferta nuevaOferta = new Oferta(1100.0, subasta);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(
      OfertaInvalidaException.class,
      () -> {
        servicioOferta.procesarOferta(1L, nuevaOferta, ofertantePrueba);
      }
    );

    verify(repositorioOfertaMock, never()).guardarOferta(nuevaOferta);
    verify(servicioAutoPujaMock, never()).procesarAutoPujas(any(Subasta.class), any(Oferta.class));
  }

  @Test
  void obtenerMejorOfertaPorSubastaDeberiaDelegarAlRepositorio() {
    Oferta ofertaEsperada = new Oferta();

    when(repositorioOfertaMock.obtenerMejorOfertaPorSubasta(1L)).thenReturn(ofertaEsperada);

    Oferta resultado = servicioOferta.obtenerMejorOfertaPorSubasta(1L);

    assertEquals(ofertaEsperada, resultado);

    verify(repositorioOfertaMock, times(1)).obtenerMejorOfertaPorSubasta(1L);
  }

  @Test
  void alIntentarOfertarEnUnaSubastaPropiaDeberiaTirarExcepcion() {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook 16gb",
      1000.0,
      5000.0,
      "Tecnologia",
      "nuevo"
    );
    subasta.setCreador(ofertantePrueba);

    Oferta nuevaOferta = new Oferta(1100.0, subasta);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(
      OfertaInvalidaException.class,
      () -> {
        servicioOferta.procesarOferta(1L, nuevaOferta, ofertantePrueba);
      }
    );

    // verificamos que no se haya guardado nada
    verify(repositorioOfertaMock, never()).guardarOferta(any(Oferta.class));
  }
}
