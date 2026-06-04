package com.tallerwebi.presentacion;

public class AutoPujaDTO {

  private double montoMaximo;
  private Long usuarioId;

  public AutoPujaDTO() {}

  public double getMontoMaximo() {
    return montoMaximo;
  }

  public void setMontoMaximo(double montoMaximo) {
    this.montoMaximo = montoMaximo;
  }

  public Long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(Long usuarioId) {
    this.usuarioId = usuarioId;
  }
}
