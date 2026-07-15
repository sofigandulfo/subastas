package com.tallerwebi.dominio.usuario;

public interface ServicioEmail {
  void notificarGanador(String emailVendedor, String emailComprador, String nombreComprador);
}
