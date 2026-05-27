package com.tallerwebi.dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private Usuario creador;

  @ManyToMany(fetch = FetchType.EAGER)
  @OrderColumn
  private List<Usuario> podio = new ArrayList<>();

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

  public Subasta() {}

  public Boolean esCreador(Usuario creador) {
    if (creador.equals(this.creador)) {
      return true;
    }
    return false;
  }

  public Usuario getCreador() {
    return creador;
  }

  public void setCreador(Usuario creador) {
    this.creador = creador;
  }

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

  public List<Usuario> getPodio() {
    return podio;
  }

  public void setPodio(List<Usuario> podio) {
    this.podio = podio;
  }

  public DetalleSubasta getDetalle() {
    return detalle;
  }

  public void setDetalle(DetalleSubasta detalle) {
    this.detalle = detalle;
  }

  @Override
  public boolean equals(Object otro) {
    if (this == otro) return true;
    if (otro == null || getClass() != otro.getClass()) return false;
    if (id == null) return false;
    return id.equals(((Subasta) otro).id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
