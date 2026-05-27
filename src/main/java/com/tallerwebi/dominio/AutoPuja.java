package com.tallerwebi.dominio;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AutoPuja {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Subasta subasta;

  @ManyToOne
  private Usuario usuario;

  private double montoMaximo;
  private boolean activa;
  private LocalDateTime fechaCreacion;

  public AutoPuja() {}

  public AutoPuja(Subasta subasta, Usuario usuario, double montoMaximo) {
    this.subasta = subasta;
    this.usuario = usuario;
    this.montoMaximo = montoMaximo;
    this.activa = true;
    this.fechaCreacion = LocalDateTime.now();
  }

  public Subasta getSubasta() {
    return subasta;
  }

  public void setSubasta(Subasta subasta) {
    this.subasta = subasta;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public double getMontoMaximo() {
    return montoMaximo;
  }

  public void setMontoMaximo(double montoMaximo) {
    this.montoMaximo = montoMaximo;
  }

  public boolean isActiva() {
    return activa;
  }

  public void setActiva(boolean activa) {
    this.activa = activa;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  @Override
  public boolean equals(Object otro) {
    if (this == otro) return true;
    if (otro == null || getClass() != otro.getClass()) return false;
    if (id == null) return false;
    return id.equals(((AutoPuja) otro).id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
