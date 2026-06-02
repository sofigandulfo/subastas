package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaOferta extends VistaWeb {

  public VistaOferta(Page page) {
    super(page);
  }

  public void escribirMonto(String monto) {
    this.completarEnElElemento("#monto", monto);
  }

  public void darClickEnOfertar() {
    this.darClickEnElElemento("#btn-ofertar");
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento("#mensaje-error-oferta");
  }
}
