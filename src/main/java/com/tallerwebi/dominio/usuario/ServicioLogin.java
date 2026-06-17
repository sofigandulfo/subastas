package com.tallerwebi.dominio.usuario;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;

public interface ServicioLogin {
  Usuario consultarUsuario(String email, String password);
  Usuario buscar(String email);
  void registrar(Usuario usuario) throws UsuarioExistente;
}
