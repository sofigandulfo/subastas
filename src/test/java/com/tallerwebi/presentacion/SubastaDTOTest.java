package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.Subasta;
import org.junit.jupiter.api.Test;

public class SubastaDTOTest {

  @Test
  public void queElMetodoEntidadConstruyeUnaSubastaCorrectamente() {
    SubastaDTO dto = new SubastaDTO();
    dto.setNombre("Sillón");
    dto.setDescripcion("Sillón de cuero");
    dto.setPrecioInicial(1000.0);
    dto.setPrecioMaximo(5000.0);
    dto.setCategoria("Hogar");
    dto.setEstadoArticulo("Nuevo");

    Subasta subasta = dto.entidad();

    assertThat(subasta, instanceOf(Subasta.class));
    assertThat(subasta.getDetalle(), notNullValue());
    assertThat(subasta.getDetalle().getNombre(), equalTo("Sillón"));
    assertThat(subasta.getPrecioInicial(), equalTo(1000.0));
  }
}
