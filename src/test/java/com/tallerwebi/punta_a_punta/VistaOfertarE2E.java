package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.VistaCrearSubasta;
import com.tallerwebi.punta_a_punta.vistas.VistaDetalleSubasta;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaOferta;
import com.tallerwebi.punta_a_punta.vistas.VistaSubastas;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaOfertarE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  Page page;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
    // browser =
    //   playwright
    //     .chromium()
    //     .launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(700));
  }

  @AfterAll
  static void cerrarNavegador() {
    playwright.close();
  }

  @BeforeEach
  void crearContextoYPagina() {
    ReiniciarDB.limpiarBaseDeDatos();
    context = browser.newContext();
    page = context.newPage();
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaPermitirOfertarEnUnaSubasta() {
    // login del usuario creador de la subasta
    VistaLogin vistaLogin = new VistaLogin(page);

    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();

    //creamis la subasta
    VistaSubastas vistaSubastas = new VistaSubastas(page);
    vistaSubastas.darClickEnNuevaSubasta();

    VistaCrearSubasta vistaCrearSubasta = new VistaCrearSubasta(page);
    vistaCrearSubasta.escribirNombre("PlayStation 5");
    vistaCrearSubasta.escribirDescripcion("Play con dos joysticks");
    vistaCrearSubasta.escribirPrecioInicial("1000");
    vistaCrearSubasta.escribirPrecioMaximo("5000");
    vistaCrearSubasta.seleccionarCategoria("Electronica");
    vistaCrearSubasta.seleccionarEstadoArticulo("Nuevo");
    vistaCrearSubasta.escribirFechaCierre("2026-12-31T23:59");
    vistaCrearSubasta.darClickEnPublicarSubasta();

    VistaDetalleSubasta vistaDetalleSubasta = new VistaDetalleSubasta(page);
    vistaDetalleSubasta.darClickEnCerrarSesion();

    VistaLogin vistaLoginOfertante = new VistaLogin(page);
    vistaLoginOfertante.escribirEMAIL("test2@unlam.edu.ar");
    vistaLoginOfertante.escribirClave("test");
    vistaLoginOfertante.darClickEnIniciarSesion();

    VistaSubastas vistaSubastasUsuarioOfertante = new VistaSubastas(page);
    vistaSubastasUsuarioOfertante.darClickEnVerDetalle();

    VistaDetalleSubasta vistaDetalleSubastaOfertante = new VistaDetalleSubasta(page);
    vistaDetalleSubastaOfertante.darClickEnOfertarManualmente();

    VistaOferta vistaOferta = new VistaOferta(page);
    vistaOferta.escribirMonto("1500");
    vistaOferta.darClickEnOfertar();
    vistaOferta.confirmarOferta();

    VistaDetalleSubasta vistaDetalleFinal = new VistaDetalleSubasta(page);

    assertThat(page.url(), containsString("/detalle-subasta"));
    assertThat(vistaDetalleFinal.obtenerPrecioActual(), containsString("1500"));
    assertThat(vistaDetalleFinal.obtenerMensajeVoyGanando(), containsString("Vas ganando"));
  }

  @Test
  void deberiaMostrarErrorSiLaOfertaEsMenorAlPrecioActual() {
    // login del usuario creador de la subasta
    VistaLogin vistaLogin = new VistaLogin(page);

    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();

    //creamis la subasta
    VistaSubastas vistaSubastas = new VistaSubastas(page);
    vistaSubastas.darClickEnNuevaSubasta();

    VistaCrearSubasta vistaCrearSubasta = new VistaCrearSubasta(page);
    vistaCrearSubasta.escribirNombre("PlayStation 5");
    vistaCrearSubasta.escribirDescripcion("Play con dos joysticks");
    vistaCrearSubasta.escribirPrecioInicial("1000");
    vistaCrearSubasta.escribirPrecioMaximo("5000");
    vistaCrearSubasta.seleccionarCategoria("Electronica");
    vistaCrearSubasta.seleccionarEstadoArticulo("Nuevo");
    vistaCrearSubasta.escribirFechaCierre("2026-12-31T23:59");
    vistaCrearSubasta.darClickEnPublicarSubasta();
    // cerramos sesion
    VistaDetalleSubasta vistaDetalleSubasta = new VistaDetalleSubasta(page);
    vistaDetalleSubasta.darClickEnCerrarSesion();

    //registramos al segundo user

    VistaLogin vistaLoginOfertante = new VistaLogin(page);
    vistaLoginOfertante.escribirEMAIL("test2@unlam.edu.ar");
    vistaLoginOfertante.escribirClave("test");
    vistaLoginOfertante.darClickEnIniciarSesion();

    VistaSubastas vistaSubastasUsuarioOfertante = new VistaSubastas(page);
    vistaSubastasUsuarioOfertante.darClickEnVerDetalle();

    VistaDetalleSubasta vistaDetalleSubastaOfertante = new VistaDetalleSubasta(page);
    vistaDetalleSubastaOfertante.darClickEnOfertarManualmente();

    VistaOferta vistaOferta = new VistaOferta(page);
    vistaOferta.escribirMonto("500");
    vistaOferta.darClickEnOfertar();
    vistaOferta.confirmarOferta();

    assertThat(page.url(), containsString("/ofertar"));
    assertThat(
      vistaOferta.obtenerMensajeDeError(),
      containsString("Error La oferta ingresada no es válida")
    );
  }

  @Test
  void deberiaRedirigirALoginSiNoEstaLogueadoEIntentaOfertar() {
    // login del usuario creador de la subasta
    VistaLogin vistaLogin = new VistaLogin(page);

    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();

    //creamis la subasta
    VistaSubastas vistaSubastas = new VistaSubastas(page);
    vistaSubastas.darClickEnNuevaSubasta();

    VistaCrearSubasta vistaCrearSubasta = new VistaCrearSubasta(page);
    vistaCrearSubasta.escribirNombre("PlayStation 5");
    vistaCrearSubasta.escribirDescripcion("Play con dos joysticks");
    vistaCrearSubasta.escribirPrecioInicial("1000");
    vistaCrearSubasta.escribirPrecioMaximo("5000");
    vistaCrearSubasta.seleccionarCategoria("Electronica");
    vistaCrearSubasta.seleccionarEstadoArticulo("Nuevo");
    vistaCrearSubasta.escribirFechaCierre("2026-12-31T23:59");
    vistaCrearSubasta.darClickEnPublicarSubasta();
    // cerramos sesion
    VistaDetalleSubasta vistaDetalleSubasta = new VistaDetalleSubasta(page);
    vistaDetalleSubasta.darClickEnCerrarSesion();

    page.navigate("http://localhost:8080/ofertar/3");

    assertThat(page.url(), containsString("/login"));
  }
}
