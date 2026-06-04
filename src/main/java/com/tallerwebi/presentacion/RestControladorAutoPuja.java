package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.autopuja.ServicioAutoPuja;
import com.tallerwebi.dominio.excepcion.AutoPujaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestControladorAutoPuja {

  private ServicioAutoPuja servicioAutoPuja;
  private ServicioSubasta servicioSubasta;

  @Autowired
  public RestControladorAutoPuja(
    ServicioAutoPuja servicioAutoPuja,
    ServicioSubasta servicioSubasta
  ) {
    this.servicioAutoPuja = servicioAutoPuja;
    this.servicioSubasta = servicioSubasta;
  }

  @PostMapping("/auto-pujar/{id}")
  public ResponseEntity<String> activarAutoPuja(
    @PathVariable("id") Long idSubasta,
    @RequestBody AutoPujaDTO autoPujaDTO
  ) {
    try {
      Usuario usuario = new Usuario();
      usuario.setId(autoPujaDTO.getUsuarioId());

      servicioSubasta.cerrarSubastasPorTiempo();
      servicioAutoPuja.activarAutoPuja(idSubasta, usuario, autoPujaDTO.getMontoMaximo());

      return ResponseEntity.ok("Auto-puja activada correctamente");
    } catch (AutoPujaInvalidaException e) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("El monto máximo ingresado no es válido para activar la auto-puja.");
    } catch (SubastaNoEncontradaException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La subasta no existe.");
    }
  }
}
