package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.oferta.Oferta;

public class OfertaDTO {

  private double monto;

  public OfertaDTO() {}

  public Oferta entidad() {
    return new Oferta(monto);
  }

  public double getMonto() {
    return monto;
  }

  public void setMonto(double monto) {
    this.monto = monto;
  }
}
