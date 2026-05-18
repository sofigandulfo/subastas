package com.tallerwebi.dominio;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
public class Subasta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double precioInicial;
  private double precioMaximo;
  private EstadoSubasta estadoSubasta;
  private double precioActual;
  private LocalDateTime fechaCierre;

  @ManyToOne
  private Usuario ganador;

  @Embedded
  private DetalleSubasta detalle;

  public Subasta(
    String nombre,
    String descripcion,
    double precioInicial,
    double precioMaximo,
    String categoria,
    String estadoArticulo
  ) {
    this.detalle = new DetalleSubasta(nombre, descripcion, categoria, estadoArticulo);
    this.precioInicial = precioInicial;
    this.precioMaximo = precioMaximo;
    this.precioActual = precioInicial;
    this.estadoSubasta = EstadoSubasta.ACTIVA;
  }

  // creo un constructor vacio para el formulario html
  public Subasta() {}

  public double getPrecioInicial() {
    return precioInicial;
  }

  public void setPrecioInicial(double precioInicial) {
    this.precioInicial = precioInicial;
  }

  public double getPrecioMaximo() {
    return precioMaximo;
  }

  public void setPrecioMaximo(double precioMaximo) {
    this.precioMaximo = precioMaximo;
  }

  public EstadoSubasta getEstadoSubasta() {
    return this.estadoSubasta;
  }

  public void setEstadoSubasta(EstadoSubasta estadoSubasta) {
    this.estadoSubasta = estadoSubasta;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public double getPrecioActual() {
    return precioActual;
  }

  public void setPrecioActual(double precioActual) {
    this.precioActual = precioActual;
  }

  public LocalDateTime getFechaCierre() {
    return fechaCierre;
  }

  public void setFechaCierre(LocalDateTime fechaCierre) {
    this.fechaCierre = fechaCierre;
  }

  public Usuario getGanador() {
    return ganador;
  }

  public void setGanador(Usuario ganador) {
    this.ganador = ganador;
  }

  public DetalleSubasta getDetalle() {
    return detalle;
  }

  public void setDetalle(DetalleSubasta detalle) {
    this.detalle = detalle;
  }
}
