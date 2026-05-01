package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioSubastaImpl implements ServicioSubasta {

  private RepositorioSubasta repositorioSubasta;

  @Autowired
  public ServicioSubastaImpl(RepositorioSubasta repositorioSubasta) {
    this.repositorioSubasta = repositorioSubasta;
  }

  @Override
  public void crearSubasta(Subasta subasta) throws SubastaInvalidaExeption {
    if (subasta.getNombre() == null || subasta.getNombre().isBlank()) {
      throw new SubastaInvalidaExeption();
    } else if (subasta.getPrecioInicial() < 0) {
      throw new SubastaInvalidaExeption();
    } else if (
      subasta.getPrecioMaximo() < 0 || subasta.getPrecioMaximo() < subasta.getPrecioInicial()
    ) {
      throw new SubastaInvalidaExeption();
    }

    subasta.setEstadoSubasta("ACTIVA");
    repositorioSubasta.guardarSubasta(subasta);
  }
}
