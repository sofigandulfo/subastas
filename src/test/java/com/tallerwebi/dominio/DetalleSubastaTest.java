package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

public class DetalleSubastaTest {

  @Test
  public void queElConstructorAsignaLosCamposCorrectamente() {
    DetalleSubasta detalle = new DetalleSubasta("Sillón", "Sillón de cuero", "Hogar", "Nuevo");

    assertThat(detalle.getNombre(), equalTo("Sillón"));
    assertThat(detalle.getDescripcion(), equalTo("Sillón de cuero"));
    assertThat(detalle.getCategoria(), equalTo("Hogar"));
    assertThat(detalle.getEstadoArticulo(), equalTo("Nuevo"));
  }

  @Test
  public void queGetImagenDevuelveUnaCopiaDelArray() {
    DetalleSubasta detalle = new DetalleSubasta();
    byte[] imagen = new byte[] { 1, 2, 3 };
    detalle.setImagen(imagen);

    assertThat(detalle.getImagen(), not(sameInstance(imagen)));
    assertThat(detalle.getImagen(), equalTo(imagen));
  }

  @Test
  public void queGetImagenDevuelveNullSiNoHayImagen() {
    DetalleSubasta detalle = new DetalleSubasta();
    assertThat(detalle.getImagen(), nullValue());
  }
}
