package com.tallerwebi.dominio.excepcion;

public class SubastaNoEditableException extends Exception {

  private static final long serialVersionUID = 1L;

  public SubastaNoEditableException() {
    super("No tenés permiso para editar esta subasta");
  }
}
