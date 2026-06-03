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
import com.tallerwebi.punta_a_punta.vistas.VistaSubastas;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaCrearSubastaE2E {

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
  void deberiaCrearUnaSubastaYMostrarSuDetalle() {
    VistaLogin vistaLogin = new VistaLogin(page);

    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();

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

    assertThat(page.url(), containsString("/spring/detalle-subasta"));
    assertThat(vistaDetalleSubasta.obtenerNombreSubasta(), containsString("PlayStation 5"));
    assertThat(
      vistaDetalleSubasta.obtenerDescripcionSubasta(),
      containsString("Play con dos joysticks")
    );
    assertThat(vistaDetalleSubasta.obtenerCategoria(), containsString("Electronica"));
  }

  @Test
  void deberiaMostrarErrorSiElPrecioMaximoEsMenorAlInicial() {
    VistaLogin vistaLogin = new VistaLogin(page);

    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();

    VistaSubastas vistaSubastas = new VistaSubastas(page);
    vistaSubastas.darClickEnNuevaSubasta();

    VistaCrearSubasta vistaCrearSubasta = new VistaCrearSubasta(page);
    vistaCrearSubasta.escribirNombre("PlayStation 5");
    vistaCrearSubasta.escribirDescripcion("Play con dos joysticks");
    vistaCrearSubasta.escribirPrecioInicial("5000");
    vistaCrearSubasta.escribirPrecioMaximo("1000");
    vistaCrearSubasta.seleccionarCategoria("Electronica");
    vistaCrearSubasta.seleccionarEstadoArticulo("Nuevo");
    vistaCrearSubasta.escribirFechaCierre("2026-12-31T23:59");
    vistaCrearSubasta.darClickEnPublicarSubasta();

    assertThat(page.url(), containsString("/spring/crear-subasta"));
    assertThat(
      vistaCrearSubasta.obtenerMensajeDeError(),
      containsString("Los datos ingresados son invalidos")
    );
  }

  @Test
  void deberiaMostrarErrorSiLaFechaDeCierreEsPasada() {
    VistaLogin vistaLogin = new VistaLogin(page);

    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();

    VistaSubastas vistaSubastas = new VistaSubastas(page);
    vistaSubastas.darClickEnNuevaSubasta();

    VistaCrearSubasta vistaCrearSubasta = new VistaCrearSubasta(page);
    vistaCrearSubasta.escribirNombre("PlayStation 5");
    vistaCrearSubasta.escribirDescripcion("Play con dos joysticks");
    vistaCrearSubasta.escribirPrecioInicial("1000");
    vistaCrearSubasta.escribirPrecioMaximo("5000");
    vistaCrearSubasta.seleccionarCategoria("Electronica");
    vistaCrearSubasta.seleccionarEstadoArticulo("Nuevo");
    vistaCrearSubasta.escribirFechaCierre("2020-01-01T10:00");
    vistaCrearSubasta.darClickEnPublicarSubasta();

    assertThat(page.url(), containsString("/spring/crear-subasta"));
    assertThat(
      vistaCrearSubasta.obtenerMensajeDeError(),
      containsString("Los datos ingresados son invalidos")
    );
  }
}
