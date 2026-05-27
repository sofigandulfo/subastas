package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.AutoPujaInvalidaException;
import com.tallerwebi.dominio.excepcion.SubastaNoEncontradaException;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioAutoPujaImpl implements ServicioAutoPuja {

  private static final double INCREMENTO = 100.0;
  private static final int CANTIDAD_MINIMA_PARA_TENER_SEGUNDA_AUTO_PUJA = 2;
  private static final int POSICION_SEGUNDA_AUTO_PUJA = 1;

  private RepositorioAutoPuja repositorioAutoPuja;
  private RepositorioOferta repositorioOferta;
  private RepositorioSubasta repositorioSubasta;

  public ServicioAutoPujaImpl(
    RepositorioAutoPuja repositorioAutoPuja,
    RepositorioOferta repositorioOferta,
    RepositorioSubasta repositorioSubasta
  ) {
    this.repositorioAutoPuja = repositorioAutoPuja;
    this.repositorioOferta = repositorioOferta;
    this.repositorioSubasta = repositorioSubasta;
  }

  @Override
  public void activarAutoPuja(long idSubasta, Usuario usuario, double montoMaximo)
    throws AutoPujaInvalidaException, SubastaNoEncontradaException {
    Subasta subasta = repositorioSubasta.obtenerSubasta(idSubasta);

    if (subasta == null) {
      throw new SubastaNoEncontradaException();
    }

    double montoOfertaInicial = subasta.getPrecioActual() + INCREMENTO;

    if (montoOfertaInicial > montoMaximo) {
      throw new AutoPujaInvalidaException();
    }

    AutoPuja autoPuja = new AutoPuja(subasta, usuario, montoMaximo);
    repositorioAutoPuja.guardar(autoPuja);

    // esta oferta no se guarda, solo sirve para disparar la logica de procesarAutoPuja
    // tiene usuario null porque no representa una oferta manual de ningun usuario
    Oferta ofertaActivacion = new Oferta(montoOfertaInicial, subasta, null);
    procesarAutoPujas(subasta, ofertaActivacion);
  }

  @Override
  public void procesarAutoPujas(Subasta subasta, Oferta ofertaManual) {
    List<AutoPuja> autoPujas = repositorioAutoPuja.obtenerAutoPujasActivasPorSubasta(
      subasta.getId()
    );
    List<AutoPuja> candidatas = new ArrayList<>();

    for (AutoPuja autoPuja : autoPujas) {
      if (puedeCompetir(autoPuja, ofertaManual, subasta)) {
        candidatas.add(autoPuja);
      }
    }

    if (!candidatas.isEmpty()) {
      AutoPuja ganadora = candidatas.get(0);
      AutoPuja segunda = obtenerSegundaAutoPuja(candidatas);

      double montoAutomatico = calcularMontoAutomatico(subasta, ganadora, segunda);
      Oferta ofertaAutomatica = new Oferta(montoAutomatico, subasta, ganadora.getUsuario());

      subasta.setPrecioActual(montoAutomatico);
      repositorioOferta.guardarOferta(ofertaAutomatica);
    }
  }

  private double calcularMontoAutomatico(Subasta subasta, AutoPuja ganadora, AutoPuja segunda) {
    double montoNecesario = segunda == null
      ? subasta.getPrecioActual() + INCREMENTO
      : segunda.getMontoMaximo() + INCREMENTO;

    return Math.min(montoNecesario, ganadora.getMontoMaximo());
  }

  private boolean esDelMismoUsuario(AutoPuja autoPuja, Oferta ofertaManual) {
    if (ofertaManual == null || ofertaManual.getUsuario() == null) {
      return false;
    }

    if (autoPuja.getUsuario() == null) {
      return false;
    }

    if (autoPuja.getUsuario().getId() == null || ofertaManual.getUsuario().getId() == null) {
      return false;
    }

    return autoPuja.getUsuario().getId().equals(ofertaManual.getUsuario().getId());
  }

  private boolean noPuedeSuperarPrecioActual(AutoPuja autoPuja, Subasta subasta) {
    return autoPuja.getMontoMaximo() < subasta.getPrecioActual() + INCREMENTO;
  }

  private boolean puedeCompetir(AutoPuja autoPuja, Oferta ofertaManual, Subasta subasta) {
    return (
      autoPuja.isActiva() &&
      !esDelMismoUsuario(autoPuja, ofertaManual) &&
      !noPuedeSuperarPrecioActual(autoPuja, subasta)
    );
  }

  private AutoPuja obtenerSegundaAutoPuja(List<AutoPuja> candidatas) {
    if (candidatas.size() < CANTIDAD_MINIMA_PARA_TENER_SEGUNDA_AUTO_PUJA) {
      return null;
    }

    return candidatas.get(POSICION_SEGUNDA_AUTO_PUJA);
  }
}
