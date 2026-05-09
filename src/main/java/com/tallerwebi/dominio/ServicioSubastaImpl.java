package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import java.io.IOException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ServicioSubastaImpl implements ServicioSubasta {

  private RepositorioSubasta repositorioSubasta;

  @Autowired
  public ServicioSubastaImpl(RepositorioSubasta repositorioSubasta) {
    this.repositorioSubasta = repositorioSubasta;
  }

  @Override
  public Subasta crearSubasta(Subasta subasta, MultipartFile imagen)
    throws SubastaInvalidaExeption {
    validarSubasta(subasta);
    if (imagen != null && !imagen.isEmpty()) {
      try {
        subasta.setImagen(imagen.getBytes());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    subasta.setEstadoSubasta("ACTIVA");
    subasta.setPrecioActual(subasta.getPrecioInicial());
    return repositorioSubasta.guardarSubasta(subasta);
  }

  private void validarSubasta(Subasta subasta) throws SubastaInvalidaExeption {
    if (subasta.getNombre() == null || subasta.getNombre().isBlank()) {
      throw new SubastaInvalidaExeption();
    } else if (subasta.getPrecioInicial() < 0) {
      throw new SubastaInvalidaExeption();
    } else if (
      subasta.getPrecioMaximo() < 0 || subasta.getPrecioMaximo() < subasta.getPrecioInicial()
    ) {
      throw new SubastaInvalidaExeption();
    }
  }

  @Override
  public Subasta obtenerSubasta(Long id) {
    return repositorioSubasta.obtenerSubasta(id);
  }
}
