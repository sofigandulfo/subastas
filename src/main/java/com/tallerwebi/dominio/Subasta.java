package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class Subasta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private String descripcion;
  private double precioInicial;
  private double precioMaximo;
  private String categoria;
  private String estadoArticulo;
  private String estadoSubasta;
  private double precioActual;

  @Lob
  private byte[] imagen;

  public Subasta(
    String nombre,
    String descripcion,
    double precioInicial,
    double precioMaximo,
    String categoria,
    String estadoArticulo
  ) {
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.precioInicial = precioInicial;
    this.precioMaximo = precioMaximo;
    this.categoria = categoria;
    this.estadoArticulo = estadoArticulo;
    this.precioActual = precioInicial;
  }

  // creo un constructor vacio para el formulario html
  public Subasta() {}

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
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

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public String getEstado() {
    return estadoArticulo;
  }

  public void setEstado(String estado) {
    this.estadoArticulo = estado;
  }

  public String getEstadoSubasta() {
    return estadoSubasta;
  }

  public void setEstadoSubasta(String estadoSubasta) {
    this.estadoSubasta = estadoSubasta;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getImagen() {
    return imagen != null ? imagen.clone() : null;
  }

  public void setImagen(byte[] imagen) {
    this.imagen = imagen != null ? imagen.clone() : null;
  }

  public double getPrecioActual() {
    return precioActual;
  }

  public void setPrecioActual(double precioActual) {
    this.precioActual = precioActual;
  }
}
