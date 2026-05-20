package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.EstadoSubasta;
import com.tallerwebi.dominio.Oferta;
import com.tallerwebi.dominio.ServicioOferta;
import com.tallerwebi.dominio.ServicioSubasta;
import com.tallerwebi.dominio.Subasta;
import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorSubasta {

  private ServicioSubasta servicioSubasta;
  private ServicioOferta servicioOferta;
  private static final String VISTA_CREAR_SUBASTA = "crear-subasta";
  private static final String KEY_SUBASTA = "subasta";

  @Autowired
  public ControladorSubasta(ServicioSubasta servicioSubasta, ServicioOferta servicioOferta) {
    this.servicioSubasta = servicioSubasta;
    this.servicioOferta = servicioOferta;
  }

  // CREAR METODOS POST Y GET! Formulario
  @GetMapping("/crear-subasta")
  public ModelAndView irAlFormulario() {
    ModelMap modelo = new ModelMap();
    modelo.put(KEY_SUBASTA, new SubastaDTO());
    return new ModelAndView(VISTA_CREAR_SUBASTA, modelo);
  }

  @PostMapping("/crear-subasta")
  public ModelAndView crearSubasta(
    @ModelAttribute("subasta") SubastaDTO subastaDTO,
    @RequestParam("imagen") MultipartFile imagen
  ) throws SubastaInvalidaExeption {
    try {
      Subasta subasta = subastaDTO.entidad();
      Subasta subastaGuardada = servicioSubasta.crearSubasta(subasta, imagen);
      return new ModelAndView("redirect:/detalle-subasta?id=" + subastaGuardada.getId());
    } catch (SubastaInvalidaExeption e) {
      return new ModelAndView(VISTA_CREAR_SUBASTA, "error", "Los datos ingresados son invalidos");
    } catch (Exception e) {
      return new ModelAndView(VISTA_CREAR_SUBASTA, "error", "Error al registrar nueva subasta");
    }
  }

  // metodo get detalle-subasta:
  @GetMapping("/detalle-subasta")
  public ModelAndView verDetalle(@RequestParam Long id) {
    servicioSubasta.cerrarSubastasPorTiempo();

    Subasta subasta = servicioSubasta.obtenerSubasta(id);
    ModelMap modelo = new ModelMap();

    if (subasta == null) {
      modelo.put(KEY_SUBASTA, new Subasta());
      return new ModelAndView(VISTA_CREAR_SUBASTA, "error", "Subasta no encontrada");
    }
    modelo.put(KEY_SUBASTA, subasta);
    if (subasta.getDetalle().getImagen() != null) {
      modelo.put(
        "imagenBase64",
        Base64.getEncoder().encodeToString(subasta.getDetalle().getImagen())
      );
    }

    if (subasta.getEstadoSubasta() == EstadoSubasta.CERRADA) {
      Oferta ofertaGanadora = servicioOferta.obtenerMejorOfertaPorSubasta(subasta.getId());

      if (ofertaGanadora != null) {
        modelo.put("ganador", ofertaGanadora.getUsuario());
        modelo.put("montoGanador", ofertaGanadora.getMonto());
      }
    }
    return new ModelAndView("detalle-subasta", modelo);
  }
}
