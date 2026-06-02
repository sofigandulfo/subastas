package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaCrearSubasta extends VistaWeb {

  public VistaCrearSubasta(Page page) {
    super(page);
  }

  public void escribirNombre(String nombre) {
    this.escribirEnElElemento("#nombre", nombre);
  }

  public void escribirDescripcion(String descripcion) {
    this.escribirEnElElemento("#descripcion", descripcion);
  }

  public void escribirPrecioInicial(String precioInicial) {
    this.completarEnElElemento("#precio-inicial", precioInicial);
  }

  public void escribirPrecioMaximo(String precioMaximo) {
    this.completarEnElElemento("#precio-maximo", precioMaximo);
  }

  public void seleccionarCategoria(String categoria) {
    page.selectOption("#categoria", categoria);
  }

  public void seleccionarEstadoArticulo(String estadoArticulo) {
    page.selectOption("#estado-articulo", estadoArticulo);
  }

  public void escribirFechaCierre(String fechaCierre) {
    this.completarEnElElemento("#fecha-cierre", fechaCierre);
  }

  public void darClickEnPublicarSubasta() {
    this.darClickEnElElemento("#btn-publicar-subasta");
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento("#mensaje-error-crear-subasta");
  }
}
