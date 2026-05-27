package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioAutoPuja;
import com.tallerwebi.dominio.ServicioSubasta;
import com.tallerwebi.dominio.Subasta;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AutoPujaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorAutoPuja {

  private ServicioAutoPuja servicioAutoPuja;
  private ServicioSubasta servicioSubasta;
  private static final String VISTA_AUTO_PUJA = "auto-puja";

  @Autowired
  public ControladorAutoPuja(ServicioAutoPuja servicioAutoPuja, ServicioSubasta servicioSubasta) {
    this.servicioAutoPuja = servicioAutoPuja;
    this.servicioSubasta = servicioSubasta;
  }

  @GetMapping("/auto-pujar/{id}")
  public ModelAndView irAFormularioAutoPuja(
    @PathVariable("id") Long idSubasta,
    HttpServletRequest request
  ) {
    if (request.getSession().getAttribute("USUARIO_ID") == null) {
      return new ModelAndView("redirect:/login");
    }

    Subasta subasta = servicioSubasta.obtenerSubasta(idSubasta);
    ModelMap modelo = new ModelMap();
    modelo.put("subasta", subasta);
    modelo.put("autoPujaDTO", new AutoPujaDTO());

    return new ModelAndView(VISTA_AUTO_PUJA, modelo);
  }

  @PostMapping("/auto-pujar/{id}")
  public ModelAndView activarAutoPuja(
    @PathVariable("id") Long idSubasta,
    @ModelAttribute("autoPujaDTO") AutoPujaDTO autoPujaDTO,
    HttpServletRequest request
  ) {
    try {
      Long usuarioId = (Long) request.getSession().getAttribute("USUARIO_ID");
      if (usuarioId == null) {
        return new ModelAndView("redirect:/login");
      }
      Usuario creadorBot = new Usuario();
      creadorBot.setId(usuarioId);

      servicioSubasta.cerrarSubastasPorTiempo();

      servicioAutoPuja.activarAutoPuja(idSubasta, creadorBot, autoPujaDTO.getMontoMaximo());

      return new ModelAndView("redirect:/detalle-subasta?id=" + idSubasta);
    } catch (AutoPujaInvalidaException e) {
      ModelMap modelo = new ModelMap();
      modelo.put("error", "El monto máximo ingresado no es válido para activar la auto-puja.");
      modelo.put("subasta", servicioSubasta.obtenerSubasta(idSubasta));
      modelo.put("autoPujaDTO", autoPujaDTO);
      return new ModelAndView(VISTA_AUTO_PUJA, modelo);
    } catch (SubastaNoEncontradaException e) {
      ModelMap modelo = new ModelMap();
      modelo.put("error", "La subasta a la que intenta acceder no existe.");
      return new ModelAndView(VISTA_AUTO_PUJA, modelo);
    }
  }
}
