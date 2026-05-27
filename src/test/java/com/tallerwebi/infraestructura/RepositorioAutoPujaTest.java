package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.AutoPuja;
import com.tallerwebi.dominio.RepositorioAutoPuja;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioAutoPujaTest {

  private RepositorioAutoPuja repositorioAutoPuja;
  private SessionFactory sessionFactoryMock;
  private Session sessionMock;
  private Query<AutoPuja> queryMock;

  @BeforeEach
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);
    queryMock = mock(Query.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    repositorioAutoPuja = new RepositorioAutoPujaImpl(sessionFactoryMock);
  }

  @Test
  public void guardarAutoPujaDeberiaLlamarAlMetodoSaveDeLaSesion() {
    AutoPuja autoPuja = new AutoPuja();

    repositorioAutoPuja.guardar(autoPuja);

    verify(sessionMock, times(1)).save(autoPuja);
  }

  @Test
  public void obtenerAutoPujasActivasPorSubastaDeberiaRetornarAutoPujasOrdenadasPorMontoYFecha() {
    AutoPuja autoPuja1 = new AutoPuja();
    AutoPuja autoPuja2 = new AutoPuja();

    when(sessionMock.createQuery(anyString(), eq(AutoPuja.class))).thenReturn(queryMock);
    when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(autoPuja1, autoPuja2));

    List<AutoPuja> resultado = repositorioAutoPuja.obtenerAutoPujasActivasPorSubasta(1L);

    assertThat(resultado.size(), equalTo(2));
    assertThat(resultado.get(0), equalTo(autoPuja1));
    assertThat(resultado.get(1), equalTo(autoPuja2));
  }
}
