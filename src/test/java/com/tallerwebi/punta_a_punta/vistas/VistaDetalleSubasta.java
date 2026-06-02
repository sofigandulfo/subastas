package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaDetalleSubasta extends VistaWeb {

  public VistaDetalleSubasta(Page page) {
    super(page);
  }

  public String obtenerNombreSubasta() {
    return this.obtenerTextoDelElemento("#nombre-subasta");
  }

  public String obtenerDescripcionSubasta() {
    return this.obtenerTextoDelElemento("#descripcion-subasta");
  }

  public String obtenerPrecioActual() {
    return this.obtenerTextoDelElemento("#precio-actual-subasta");
  }

  public String obtenerCategoria() {
    return this.obtenerTextoDelElemento("#categoria-subasta");
  }

  public void darClickEnOfertarManualmente() {
    this.darClickEnElElemento("#btn-ofertar-manualmente");
  }

  public String obtenerMensajeVoyGanando() {
    return this.obtenerTextoDelElemento("#mensaje-voy-ganando");
  }

  public void darClickEnCerrarSesion() {
    this.darClickEnElElemento("#btn-cerrar-sesion-detalle");
  }
}
