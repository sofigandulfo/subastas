package com.tallerwebi.dominio.excepcion;

public class SubastaConOfertasException extends Exception {

  private static final long serialVersionUID = 1L;

  public SubastaConOfertasException() {
    super("No se puede eliminar una subasta que ya tiene ofertas");
  }
}
