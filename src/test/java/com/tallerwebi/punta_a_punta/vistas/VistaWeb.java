package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.net.MalformedURLException;
import java.net.URL;

public class VistaWeb {

  protected Page page;

  public VistaWeb(Page page) {
    this.page = page;
  }

  public URL obtenerURLActual() throws MalformedURLException {
    URL url = new URL(page.url());
    return url;
  }

  protected String obtenerTextoDelElemento(String selectorCSS) {
    return this.obtenerElemento(selectorCSS).textContent();
  }

  protected void darClickEnElElemento(String selectorCSS) {
    this.obtenerElemento(selectorCSS).click();
  }

  protected void escribirEnElElemento(String selectorCSS, String texto) {
    this.obtenerElemento(selectorCSS).type(texto);
  }

  //agregamos este metodo para rellenar el contenido reemplazandolo
  // en escribir elemento playwright escribe encima del valor que ya tenia el input
  // (como en los campos de los precios de crear subasta empiezan en 0.0, si el numero es 1000.0 escribe 10000.0)
  protected void completarEnElElemento(String selectorCSS, String texto) {
    this.obtenerElemento(selectorCSS).fill(texto);
  }

  private Locator obtenerElemento(String selectorCSS) {
    return page.locator(selectorCSS);
  }
}
