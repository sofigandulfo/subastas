package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AutoPujaTest {

  @Test
  public void sePuedeCrearUnaAutoPujaConUsuairoSubastaYMontoMaximo() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);

    Subasta subasta = new Subasta(
      "Notebook",
      "Notebook gamer",
      1000.0,
      10000.0,
      "Tecnologia",
      "Nuevo"
    );

    AutoPuja autoPuja = new AutoPuja(subasta, usuario, 5000.0);

    assertEquals(usuario, autoPuja.getUsuario());
    assertEquals(subasta, autoPuja.getSubasta());
    assertEquals(5000.0, autoPuja.getMontoMaximo());
    assertTrue(autoPuja.isActiva());
    assertNotNull(autoPuja.getFechaCreacion());
  }
}
