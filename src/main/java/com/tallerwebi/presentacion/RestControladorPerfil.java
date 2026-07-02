package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.oferta.ServicioOferta;
import com.tallerwebi.dominio.subasta.ServicioSubasta;
import com.tallerwebi.dominio.subasta.Subasta;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfil")
public class RestControladorPerfil {

  private final ServicioOferta servicioOferta;
  private final ServicioSubasta servicioSubasta;

  @Autowired
  public RestControladorPerfil(ServicioOferta servicioOferta, ServicioSubasta servicioSubasta) {
    this.servicioOferta = servicioOferta;
    this.servicioSubasta = servicioSubasta;
  }

  @GetMapping("/ventas")
  public ResponseEntity<List<PerfilSubastaDTO>> obtenerMisVentas(
    @RequestParam Long usuarioId,
    @RequestParam int pagina,
    @RequestParam int limite
  ) {
    List<Subasta> misVentas = servicioSubasta.obtenerSubastasDelCreador(usuarioId);
    return ResponseEntity.ok(paginarYConvertir(misVentas, pagina, limite, usuarioId));
  }

  @GetMapping("/pujas")
  public ResponseEntity<List<PerfilSubastaDTO>> obtenerMisPujas(
    @RequestParam Long usuarioId,
    @RequestParam int pagina,
    @RequestParam int limite
  ) {
    List<Subasta> misPujas = servicioOferta.obtenerSubastasDondeParticipe(usuarioId);
    return ResponseEntity.ok(paginarYConvertir(misPujas, pagina, limite, usuarioId));
  }

  private List<PerfilSubastaDTO> paginarYConvertir(
    List<Subasta> listaCompleta,
    int pagina,
    int limite,
    Long usuarioId
  ) {
    if (listaCompleta == null || listaCompleta.isEmpty()) return new ArrayList<>();

    List<Subasta> listaOrdenada = listaCompleta
      .stream()
      .sorted(java.util.Comparator.comparing(Subasta::getId).reversed())
      .collect(Collectors.toList());

    int desde = pagina * limite;
    if (desde >= listaOrdenada.size()) return new ArrayList<>();

    int hasta = Math.min(desde + limite, listaOrdenada.size());
    List<Subasta> sublista = listaOrdenada.subList(desde, hasta);

    return sublista
      .stream()
      .map(s -> {
        Oferta mejorOferta = servicioOferta.obtenerMejorOfertaPorSubasta(s.getId());
        boolean vaGanandoActual =
          mejorOferta != null && mejorOferta.getUsuario().getId().equals(usuarioId);

        return new PerfilSubastaDTO(s, usuarioId, vaGanandoActual);
      })
      .collect(Collectors.toList());
  }
}
