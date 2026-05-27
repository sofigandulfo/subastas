package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioOferta;
import com.tallerwebi.dominio.ServicioSubasta;
import com.tallerwebi.dominio.Subasta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

        modelo.put("misVentas", misVentas);
        modelo.put("misPujas", misPujas);
        modelo.put("usuarioId", usuarioId); // Clave para saber si ganó o perdió en el HTML

        return new ModelAndView("perfil", modelo);
    }
}
