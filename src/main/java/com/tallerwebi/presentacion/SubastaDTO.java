package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Subasta;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class SubastaDTO {

  private String nombre;

  private String descripcion;
  private double precioInicial;
  private double precioMaximo;
  private String categoria;
  private String estadoArticulo;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaCierre;

  public SubastaDTO() {}

  public Subasta entidad() {
    Subasta subasta = new Subasta(
      nombre,
      descripcion,
      precioInicial,
      precioMaximo,
      categoria,
      estadoArticulo
    );
    subasta.setFechaCierre(fechaCierre);
    return subasta;
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

  public String getEstadoArticulo() {
    return estadoArticulo;
  }

  public void setEstadoArticulo(String estadoArticulo) {
    this.estadoArticulo = estadoArticulo;
  }

  public LocalDateTime getFechaCierre() {
    return fechaCierre;
  }

  public void setFechaCierre(LocalDateTime fechaCierre) {
    this.fechaCierre = fechaCierre;
  }
}
