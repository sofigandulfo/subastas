package com.tallerwebi.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Oferta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Muchas ofertas pertenecen a una única subasta
  @ManyToOne
  private Subasta subasta;

  @ManyToOne
  private Usuario usuario;

  private double monto;

  public Oferta(double monto, Subasta subasta) {
    this.monto = monto;
    this.subasta = subasta;
  }

  public Oferta(double monto, Subasta subasta, Usuario usuario) {
    this.monto = monto;
    this.subasta = subasta;
    this.usuario = usuario;
  }

  public Oferta() {}

  public Oferta(double monto) {
    this.monto = monto;
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public double getMonto() {
    return this.monto;
  }

  public void setMonto(double nuevoMonto) {
    this.monto = nuevoMonto;
  }

  public Subasta getSubasta() {
    return this.subasta;
  }

  public void setSubasta(Subasta subasta) {
    this.subasta = subasta;
  }
}
