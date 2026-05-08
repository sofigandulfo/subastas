package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioSubastaImpl implements ServicioSubasta {

  private RepositorioSubasta repositorioSubasta;

  @Autowired
  public ServicioSubastaImpl(RepositorioSubasta repositorioSubasta) {
    this.repositorioSubasta = repositorioSubasta;
  }

  @Override
  public Subasta crearSubasta(Subasta subasta) throws SubastaInvalidaExeption {
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

    // Agrego esta linea para que el precio actual de la subasta se guarde en la BD
    subasta.setPrecioActual(subasta.getPrecioInicial());

    return repositorioSubasta.guardarSubasta(subasta);
  }

  @Override
  public Subasta obtenerSubasta(Long id) {
    return repositorioSubasta.obtenerSubasta(id);
  }
}
