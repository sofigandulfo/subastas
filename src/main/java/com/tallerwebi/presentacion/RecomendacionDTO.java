package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.subasta.Subasta;
import java.util.Base64;

public class RecomendacionDTO {

  private Long id;
  private String nombre;
  private String descripcion;
  private String categoria;
  private double precioActual;
  private String imagen;

  public RecomendacionDTO(Subasta subasta) {
    this.id = subasta.getId();
    this.nombre = subasta.getDetalle().getNombre();
    this.descripcion = subasta.getDetalle().getDescripcion();
    this.categoria = subasta.getDetalle().getCategoria();
    this.precioActual = subasta.getPrecioActual();

    if (subasta.getDetalle().getImagen() != null) {
      this.imagen = Base64.getEncoder().encodeToString(subasta.getDetalle().getImagen());
    }
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

  public String getImagen() {
    return imagen;
  }
}
