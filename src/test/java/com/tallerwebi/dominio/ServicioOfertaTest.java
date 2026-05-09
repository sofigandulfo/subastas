package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioOfertaTest {

  private RepositorioSubasta repositorioSubastaMock;
  private RepositorioOferta repositorioOfertaMock;
  private ServicioOferta servicioOferta;

  @BeforeEach
  public void init() {
    repositorioSubastaMock = mock(RepositorioSubasta.class);
    repositorioOfertaMock = mock(RepositorioOferta.class);
    // Le inyectamos ambos mocks al constructor del servicio
    servicioOferta = new ServicioOfertaImpl(repositorioSubastaMock, repositorioOfertaMock);
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

    servicioOferta.procesarOferta(1L, nuevaOferta);

    // Validamos que el precio actual de la subasta se actualizó
    assertEquals(1100.0, subasta.getPrecioActual());

    // Ademas validamos que el servicio haya llamado al metodo guardarOferta
    verify(repositorioOfertaMock, times(1)).guardarOferta(nuevaOferta);
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
        servicioOferta.procesarOferta(1L, nuevaOferta);
      }
    );
  }

  @Test
  void alIntentarHacerUnaOfertaDeUnObjetoDeUnaSubastaConNombreVacioDeberiaTirarUnaExcepcion() {
    Subasta subasta = new Subasta("", "Notebook 16gb", 1000.0, 5000.0, "Tecnologia", "nuevo");
    Oferta nuevaOferta = new Oferta(1100.0, subasta);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(
      SubastaNoEncontradaException.class,
      () -> {
        servicioOferta.procesarOferta(1L, nuevaOferta);
      }
    );
  }
}
