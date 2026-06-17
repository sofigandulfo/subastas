package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.autopuja.AutoPuja;
import com.tallerwebi.dominio.autopuja.RepositorioAutoPuja;
import com.tallerwebi.dominio.autopuja.ServicioAutoPuja;
import com.tallerwebi.dominio.autopuja.ServicioAutoPujaImpl;
import com.tallerwebi.dominio.excepcion.AutoPujaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.oferta.RepositorioOferta;
import com.tallerwebi.dominio.subasta.RepositorioSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.dominio.usuario.Usuario;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioAutoPujaTest {

  private RepositorioAutoPuja repositorioAutoPujaMock;
  private RepositorioOferta repositorioOfertaMock;
  private RepositorioSubasta repositorioSubastaMock;
  private ServicioAutoPuja servicioAutoPuja;

  @BeforeEach
  public void init() {
    repositorioAutoPujaMock = mock(RepositorioAutoPuja.class);
    repositorioOfertaMock = mock(RepositorioOferta.class);
    repositorioSubastaMock = mock(RepositorioSubasta.class);

    servicioAutoPuja =
      new ServicioAutoPujaImpl(
        repositorioAutoPujaMock,
        repositorioOfertaMock,
        repositorioSubastaMock
      );
  }

  private Usuario usuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private Subasta subastaConId(Long id) {
    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook gamer",
      1000.0,
      10000.0,
      "Tecnologia",
      "Nuevo"
    );
    subasta.setId(id);
    return subasta;
  }

  @Test
  public void alActivarAutoPujaDebeGuardarLaConfiguracionYCrearUnaOfertaInicial()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual((1000.0));

    Usuario usuario = usuarioConId(2L);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    AutoPuja autoPuja = new AutoPuja(subasta, usuario, 5000.0);
    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L))
      .thenReturn(List.of(autoPuja));

    servicioAutoPuja.activarAutoPuja(1L, usuario, 5000.0);

    verify(repositorioAutoPujaMock, times(1)).guardar(any(AutoPuja.class));
    verify(repositorioOfertaMock, times(1)).guardarOferta(any(Oferta.class));

    assertEquals(1100.0, subasta.getPrecioActual());
  }

  @Test
  public void noDebeActivarAutoPujaSiElMontoMaximoNoSuperaElPrecioActualMasIncremento() {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(1000.0);

    Usuario usuario = usuarioConId(2L);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    assertThrows(
      AutoPujaInvalidaException.class,
      () -> {
        servicioAutoPuja.activarAutoPuja(1L, usuario, 1050.0);
      }
    );

    verify(repositorioAutoPujaMock, never()).guardar(any(AutoPuja.class));
    verify(repositorioOfertaMock, never()).guardarOferta(any(Oferta.class));
  }

  @Test
  public void cuandoOtroUsuarioOfertaElBotDebeResponderSiNoSuperaSuMontoMaximo() {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(2000.0);

    Usuario usuarioQueOferto = usuarioConId(2L);
    Usuario usuarioConBot = usuarioConId(3L);

    Oferta ofertaManual = new Oferta(2000.0, subasta, usuarioQueOferto);
    AutoPuja autoPuja = new AutoPuja(subasta, usuarioConBot, 5000.0);

    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L))
      .thenReturn(List.of(autoPuja));

    servicioAutoPuja.procesarAutoPujas(subasta, ofertaManual);

    verify(repositorioOfertaMock, times(1)).guardarOferta(any(Oferta.class));
    assertEquals(2100.0, subasta.getPrecioActual());
  }

  @Test
  public void elBotNoDebePujarContraUnaOfertaDelMismoUsuario() {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(2000.0);

    Usuario usuario = usuarioConId(2L);

    Oferta ofertaManual = new Oferta(2000.0, subasta, usuario);
    AutoPuja autoPuja = new AutoPuja(subasta, usuario, 5000.0);

    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L))
      .thenReturn(List.of(autoPuja));

    servicioAutoPuja.procesarAutoPujas(subasta, ofertaManual);

    verify(repositorioOfertaMock, never()).guardarOferta(any(Oferta.class));
    assertEquals(2000.0, subasta.getPrecioActual());
  }

  @Test
  public void siHayVariasAutoPujasEnUnaSubastaDebeGanarLaDeMayorMontoMaximo() {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(2000.0);
    // simulamos que la oferta manual fue aceptada y aplicada enla subasta
    Oferta ofertaManual = new Oferta(2000.0, subasta, usuarioConId(4L));

    Usuario usuario1 = usuarioConId(2L);
    Usuario usuario2 = usuarioConId(3L);

    AutoPuja autoPujaUsuario1 = new AutoPuja(subasta, usuario1, 5000.0);
    AutoPuja autoPujaUsuario2 = new AutoPuja(subasta, usuario2, 7000.0);

    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L))
      .thenReturn(List.of(autoPujaUsuario2, autoPujaUsuario1));

    servicioAutoPuja.procesarAutoPujas(subasta, ofertaManual);

    verify(repositorioOfertaMock, times(1)).guardarOferta(any(Oferta.class));
    verify(repositorioOfertaMock)
      .guardarOferta(
        argThat(oferta ->
          oferta.getUsuario().getId().equals(usuario2.getId()) && oferta.getMonto() == 5100.0
        )
      );
    assertEquals(5100.0, subasta.getPrecioActual());
  }

  @Test
  public void alActivarAutoPujaMenorAOtraExistenteDebeGanarLaExistente()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(1100.0);

    Usuario usuarioExistente = usuarioConId(2L);
    Usuario usuarioNuevo = usuarioConId(3L);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    AutoPuja autoPujaExistente = new AutoPuja(subasta, usuarioExistente, 7000.0);
    AutoPuja autoPujaNueva = new AutoPuja(subasta, usuarioNuevo, 5000.0);

    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L))
      .thenReturn(List.of(autoPujaExistente, autoPujaNueva));

    servicioAutoPuja.activarAutoPuja(1L, usuarioNuevo, 5000.0);

    verify(repositorioAutoPujaMock, times(1)).guardar(any(AutoPuja.class));
    verify(repositorioOfertaMock, times(1)).guardarOferta(any(Oferta.class));
    verify(repositorioOfertaMock)
      .guardarOferta(
        argThat(oferta ->
          oferta.getUsuario().getId().equals(usuarioExistente.getId()) &&
          oferta.getMonto() == 5100.0
        )
      );
    assertEquals(5100.0, subasta.getPrecioActual());
  }

  @Test
  public void alActivarAutoPujaMayorAOtraExistenteDebeGanarLaNueva()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(1100.0);

    Usuario usuarioExistente = usuarioConId(2L);
    Usuario usuarioNuevo = usuarioConId(3L);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    AutoPuja autoPujaExistente = new AutoPuja(subasta, usuarioExistente, 5000.0);
    AutoPuja autoPujaNueva = new AutoPuja(subasta, usuarioNuevo, 7000.0);

    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L))
      .thenReturn(List.of(autoPujaNueva, autoPujaExistente));

    servicioAutoPuja.activarAutoPuja(1L, usuarioNuevo, 7000.0);

    verify(repositorioAutoPujaMock, times(1)).guardar(any(AutoPuja.class));
    verify(repositorioOfertaMock)
      .guardarOferta(
        argThat(oferta ->
          oferta.getUsuario().getId().equals(usuarioNuevo.getId()) && oferta.getMonto() == 5100.0
        )
      );
    assertEquals(5100.0, subasta.getPrecioActual());
  }

  @Test
  public void siDosAutoPujasTieneElMismoMontoMaximoDebeGanarLaPrimeraEnAutoPujar()
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(1100.0);

    Usuario usuarioExistente = usuarioConId(2L);
    Usuario usuarioNuevo = usuarioConId(3L);

    when(repositorioSubastaMock.obtenerSubasta(1L)).thenReturn(subasta);

    AutoPuja autoPujaExistente = new AutoPuja(subasta, usuarioExistente, 5000.0);
    AutoPuja autoPujaNueva = new AutoPuja(subasta, usuarioNuevo, 5000.0);

    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L))
      .thenReturn(List.of(autoPujaExistente, autoPujaNueva));

    servicioAutoPuja.activarAutoPuja(1L, usuarioNuevo, 5000.0);

    verify(repositorioAutoPujaMock, times(1)).guardar(any(AutoPuja.class));
    verify(repositorioOfertaMock, times(1)).guardarOferta(any(Oferta.class));
    verify(repositorioOfertaMock)
      .guardarOferta(
        argThat(oferta ->
          oferta.getUsuario().getId().equals(usuarioExistente.getId()) &&
          oferta.getMonto() == 5000.0
        )
      );
    assertEquals(5000.0, subasta.getPrecioActual());
  }

  @Test
  public void procesarAutoPujasNoDeberiaHacerNadaSiNoHayAutoPujasActivas() {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(2000.0);
    Oferta ofertaManual = new Oferta(2000.0, subasta, usuarioConId(2L));

    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L)).thenReturn(List.of());

    servicioAutoPuja.procesarAutoPujas(subasta, ofertaManual);

    verify(repositorioOfertaMock, never()).guardarOferta(any());
  }

  @Test
  public void activarAutoPujaDeberiaLanzarExcepcionSiLaSubastaNoExiste() {
    when(repositorioSubastaMock.obtenerSubasta(99L)).thenReturn(null);

    assertThrows(
      SubastaNoEncontradaException.class,
      () -> {
        servicioAutoPuja.activarAutoPuja(99L, usuarioConId(1L), 5000.0);
      }
    );

    verify(repositorioAutoPujaMock, never()).guardar(any());
    verify(repositorioOfertaMock, never()).guardarOferta(any());
  }

  @Test
  public void elBotNoDebePujarSiSuMontoMaximoYaFueSuperado() {
    Subasta subasta = subastaConId(1L);
    subasta.setPrecioActual(6000.0); // precio actual ya supera el monto máximo del bot

    Usuario usuarioQueOferto = usuarioConId(2L);
    Usuario usuarioConBot = usuarioConId(3L);

    Oferta ofertaManual = new Oferta(6000.0, subasta, usuarioQueOferto);
    AutoPuja autoPuja = new AutoPuja(subasta, usuarioConBot, 5000.0); // monto max menor al precio actual

    when(repositorioAutoPujaMock.obtenerAutoPujasActivasPorSubasta(1L))
      .thenReturn(List.of(autoPuja));

    servicioAutoPuja.procesarAutoPujas(subasta, ofertaManual);

    verify(repositorioOfertaMock, never()).guardarOferta(any());
    assertEquals(6000.0, subasta.getPrecioActual());
  }
}
