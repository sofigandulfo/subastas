package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.OfertaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import com.tallerwebi.dominio.oferta.ServicioOferta;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.dominio.usuario.Usuario;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorOferta {

  private ServicioOferta servicioOferta;
  private ServicioSubasta servicioSubasta;
  private static final String VISTA_OFERTA = "oferta";

  public ControladorOferta(ServicioOferta servicioOfertaMock, ServicioSubasta servicioSubastaMock) {
    this.servicioOferta = servicioOfertaMock;
    this.servicioSubasta = servicioSubastaMock;
  }

  @GetMapping("/ofertar/{id}")
  public ModelAndView irAFormularioOferta(
    @PathVariable("id") Long idSubasta,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute("USUARIO_ID") == null) {
      return new ModelAndView("redirect:/login");
    }
    // 1. Buscamos la subasta para que el HTML pueda mostrar el precio actual
    Subasta subasta = servicioSubasta.obtenerSubasta(idSubasta);
    ModelMap modelo = new ModelMap();
    modelo.put("subasta", subasta);

    // 2. Mandamos un objeto Oferta vacío para que Thymeleaf pueda "atar" el monto
    // que el usuario escriba en el input
    modelo.put(VISTA_OFERTA, new OfertaDTO());

    // 3. Devolvemos la vista "oferta" con el bolso cargado
    return new ModelAndView(VISTA_OFERTA, modelo);
  }

  @PostMapping("/ofertar/{id}")
  public ModelAndView realizarOferta(
    @PathVariable("id") Long idSubasta,
    @ModelAttribute(VISTA_OFERTA) OfertaDTO ofertaDTO,
    HttpServletRequest request
  ) {
    try {
      Long usuarioId = (Long) request.getSession().getAttribute("USUARIO_ID");
      if (usuarioId == null) {
        return new ModelAndView("redirect:/login");
      }
      Usuario ofertante = new Usuario();
      ofertante.setId(usuarioId);

      servicioSubasta.cerrarSubastasPorTiempo();
      servicioOferta.procesarOferta(idSubasta, ofertaDTO.entidad(), ofertante);

      request.getSession().removeAttribute("RECOMENDACIONES_IDS");
      return new ModelAndView("redirect:/detalle-subasta?id=" + idSubasta);
    } catch (OfertaInvalidaException e) {
      ModelMap modelo = new ModelMap();
      modelo.put("error", "La oferta ingresada no es válida.");
      modelo.put("subasta", servicioSubasta.obtenerSubasta(idSubasta));
      modelo.put(VISTA_OFERTA, ofertaDTO);
      return new ModelAndView(VISTA_OFERTA, modelo);
    } catch (SubastaNoEncontradaException e) {
      ModelMap modelo = new ModelMap();
      modelo.put("error", "La subasta a la que intenta ofertar no existe.");
      return new ModelAndView(VISTA_OFERTA, modelo);
    }
  }
}
