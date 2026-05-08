package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioSubasta;
import com.tallerwebi.dominio.Subasta;
import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorSubasta {

  private ServicioSubasta servicioSubasta;
  private static final String VISTA_CREAR_SUBASTA = "crear-subasta";
  private static final String KEY_SUBASTA = "subasta";

  @Autowired
  public ControladorSubasta(ServicioSubasta servicioSubasta) {
    this.servicioSubasta = servicioSubasta;
  }

  // CREAR METODOS POST Y GET! Formulario
  @GetMapping("/crear-subasta")
  public ModelAndView irAlFormulario() {
    ModelMap modelo = new ModelMap();
    modelo.put(KEY_SUBASTA, new Subasta());
    return new ModelAndView(VISTA_CREAR_SUBASTA, modelo);
  }

  @PostMapping("/crear-subasta")
  public ModelAndView crearSubasta(@ModelAttribute("subasta") Subasta subasta)
    throws SubastaInvalidaExeption {
    try {
      Subasta subastaGuardada = servicioSubasta.crearSubasta(subasta);
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
    Subasta subasta = servicioSubasta.obtenerSubasta(id);
    ModelMap modelo = new ModelMap();
    if (subasta == null) {
      modelo.put(KEY_SUBASTA, new Subasta());
      return new ModelAndView(VISTA_CREAR_SUBASTA, "error", "Subasta no encontrada");
    }
    modelo.put(KEY_SUBASTA, subasta);
    return new ModelAndView("detalle-subasta", modelo);
  }
}
