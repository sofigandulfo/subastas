package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaSubastas extends VistaWeb {

  public VistaSubastas(Page page) {
    super(page);
  }

  public String obtenerTitulo() {
    return this.obtenerTextoDelElemento("#titulo-subastas");
  }

  public void darClickEnNuevaSubasta() {
    this.darClickEnElElemento("#btn-nueva-subasta");
  }

  public void darClickEnPerfil() {
    this.darClickEnElElemento("#btn-mi-perfil");
  }

  public void darClickEnCerrarSesion() {
    this.darClickEnElElemento("#btn-cerrar-sesion");
  }

  public void darClickEnVerDetalle() {
    page.locator(".btn-ver-detalle").last().click();
  }
}
