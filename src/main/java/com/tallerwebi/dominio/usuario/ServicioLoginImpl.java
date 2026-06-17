package com.tallerwebi.dominio.usuario;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioLoginImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    return repositorioUsuario.buscarUsuario(email, password);
  }

  @Override
  public Usuario buscar(String email) {
    return repositorioUsuario.buscar(email);
  }

  @Override
  public void registrar(Usuario usuario) throws UsuarioExistente {
    Usuario usuarioEncontrado = repositorioUsuario.buscar(usuario.getEmail());
    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }
    repositorioUsuario.guardar(usuario);
  }
}
