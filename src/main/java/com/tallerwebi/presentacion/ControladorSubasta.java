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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorSubasta {

  private ServicioSubasta servicioSubasta;

  @Autowired
  public ControladorSubasta(ServicioSubasta servicioSubasta) {
    this.servicioSubasta = servicioSubasta;
  }

  // CREAR METODOS POST Y GET! Formulario
  @GetMapping("/crear-subasta")
  public ModelAndView irAlFormulario() {
    ModelMap modelo = new ModelMap();
    modelo.put("subasta", new Subasta());
    return new ModelAndView("crear-subasta", modelo);
  }

  @PostMapping("/crear-subasta")
  public ModelAndView crearSubasta(@ModelAttribute("subasta") Subasta subasta) { // el controlador recibe el
    // objeto del formulario
    ModelMap modelo = new ModelMap();
    try {
      servicioSubasta.crearSubasta(subasta);
    } catch (SubastaInvalidaExeption e) {
      modelo.put("error", "Los datos ingresados son invalidos");
      return new ModelAndView("crear-subasta", modelo);
    } catch (Exception e) {
      modelo.put("error", "Error al registrar nueva subasta");
      return new ModelAndView("crear-subasta", modelo);
    }
    return new ModelAndView("redirect:/detalle-subasta");
  }
}
