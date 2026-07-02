package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.subasta.EstadoSubasta;
import com.tallerwebi.dominio.subasta.Subasta;

public class PerfilSubastaDTO {

  private Long id;
  private String nombre;
  private String estado;
  private boolean vendido;
  private boolean vasGanando;
  private boolean ganaste;

  public PerfilSubastaDTO(Subasta subasta, Long usuarioId, boolean vaGanandoActual) {
    this.id = subasta.getId();
    this.nombre = subasta.getDetalle().getNombre();
    this.estado = subasta.getEstadoSubasta().name();
    this.vendido =
      subasta.getEstadoSubasta() == EstadoSubasta.CERRADA &&
      subasta.getPodio() != null &&
      !subasta.getPodio().isEmpty();
    this.vasGanando = vaGanandoActual;
    this.ganaste = false;

    if (
      subasta.getEstadoSubasta() == EstadoSubasta.CERRADA &&
      subasta.getPodio() != null &&
      !subasta.getPodio().isEmpty()
    ) {
      this.ganaste = subasta.getPodio().get(0).getId().equals(usuarioId);
    }
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getEstado() {
    return estado;
  }

  public boolean isVendido() {
    return vendido;
  }

  public boolean isVasGanando() {
    return vasGanando;
  }

  public boolean isGanaste() {
    return ganaste;
  }
}
