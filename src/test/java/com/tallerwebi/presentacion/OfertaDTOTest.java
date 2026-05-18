package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import com.tallerwebi.dominio.Oferta;
import org.junit.jupiter.api.Test;

public class OfertaDTOTest {

  @Test
  public void queElMetodoEntidadConstruyeUnaOfertaCorrectamente() {
    OfertaDTO dto = new OfertaDTO();
    dto.setMonto(1500.0);

    Oferta oferta = dto.entidad();

    assertThat(oferta, instanceOf(Oferta.class));
    assertThat(oferta.getMonto(), equalTo(1500.0));
  }
}
