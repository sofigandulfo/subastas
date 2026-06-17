package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.subasta.Subasta;

public class RecomendacionDTO {

  private Long id;
  private String nombre;
  private String descripcion;
  private String categoria;
  private double precioActual;

  public RecomendacionDTO(Subasta subasta) {
    this.id = subasta.getId();
    this.nombre = subasta.getDetalle().getNombre();
    this.descripcion = subasta.getDetalle().getDescripcion();
    this.categoria = subasta.getDetalle().getCategoria();
    this.precioActual = subasta.getPrecioActual();
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getCategoria() {
    return categoria;
  }

  public double getPrecioActual() {
    return precioActual;
  }
}
