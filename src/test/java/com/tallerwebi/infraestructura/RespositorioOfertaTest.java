package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.oferta.Oferta;
import com.tallerwebi.dominio.oferta.RepositorioOferta;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RespositorioOfertaTest {

  private RepositorioOferta repositorioOferta;
  private SessionFactory sessionFactoryMock;
  private Session sessionMock;

  @BeforeEach
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    repositorioOferta = new RepositorioOfertaImpl(sessionFactoryMock);
  }

  @Test
  public void guardarOfertaDeberiaLlamarAlMetodoSaveDeLaSesion() {
    Oferta oferta = new Oferta();

    repositorioOferta.guardarOferta(oferta);

    verify(sessionMock, times(1)).save(oferta);
  }

  @Test
  public void obtenerMejoresOfertasPorSubastaDeberiaRetornarOfertasOrdenadasPorMonto() {
    // preparacion
    Oferta oferta1 = new Oferta();
    Oferta oferta2 = new Oferta();
    org.hibernate.query.Query queryMock = mock(org.hibernate.query.Query.class);

    when(sessionMock.createQuery(anyString(), eq(Oferta.class))).thenReturn(queryMock);
    when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(oferta1, oferta2));

    // ejecucion
    List<Oferta> resultado = repositorioOferta.obtenerMejoresOfertasPorSubasta(1L);

    // validacion
    assertThat(resultado.size(), equalTo(2));
    assertThat(resultado.get(0), equalTo(oferta1));
    assertThat(resultado.get(1), equalTo(oferta2));
  }
}
