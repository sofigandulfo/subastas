package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Subasta;

public class SubastaDTO {

  private String nombre;

  private String descripcion;
  private double precioInicial;
  private double precioMaximo;
  private String categoria;
  private String estadoArticulo;

  public SubastaDTO() {}

  public Subasta entidad() {
    return new Subasta(nombre, descripcion, precioInicial, precioMaximo, categoria, estadoArticulo);
  }

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

  public String estadoArticulo() {
    return estadoArticulo;
  }

  public void setEstado(String estadoArticulo) {
    this.estadoArticulo = estadoArticulo;
  }
}
