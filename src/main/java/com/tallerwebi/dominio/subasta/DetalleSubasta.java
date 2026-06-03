package com.tallerwebi.dominio.subasta;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class DetalleSubasta {

  private String nombre;
  private String descripcion;
  private String categoria;
  private String estadoArticulo;

  @Lob
  private byte[] imagen;

  public DetalleSubasta() {}

  public DetalleSubasta(
    String nombre,
    String descripcion,
    String categoria,
    String estadoArticulo
  ) {
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.estadoArticulo = estadoArticulo;
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

  public byte[] getImagen() {
    return imagen == null ? null : imagen.clone();
  }

  public void setImagen(byte[] imagen) {
    this.imagen = imagen == null ? null : imagen.clone();
  }
}
