package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.oferta.ServicioOferta;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPerfil {

  private ServicioSubasta servicioSubasta;
  private ServicioOferta servicioOferta;

  @Autowired
  public ControladorPerfil(ServicioSubasta servicioSubasta, ServicioOferta servicioOferta) {
    this.servicioSubasta = servicioSubasta;
    this.servicioOferta = servicioOferta;
  }

  @GetMapping("/perfil")
  public ModelAndView verPerfil(HttpServletRequest request) {
    Long usuarioId = (Long) request.getSession().getAttribute("USUARIO_ID");

    if (usuarioId == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();

    List<Subasta> misVentas = servicioSubasta.obtenerSubastasDelCreador(usuarioId);
    List<Subasta> misPujas = servicioOferta.obtenerSubastasDondeParticipe(usuarioId);

    Map<Long, Boolean> estadoPujas = new HashMap<>();

    for (Subasta puja : misPujas) {
      Oferta mejorOferta = servicioOferta.obtenerMejorOfertaPorSubasta(puja.getId());
      boolean vaGanando =
        (mejorOferta != null && mejorOferta.getUsuario().getId().equals(usuarioId));
      estadoPujas.put(puja.getId(), vaGanando);
    }

    modelo.put("estadoPujas", estadoPujas);
    modelo.put("misVentas", misVentas);
    modelo.put("misPujas", misPujas);
    modelo.put("usuarioId", usuarioId);

    return new ModelAndView("perfil", modelo);
  }
}
