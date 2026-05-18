package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioSubasta;
import com.tallerwebi.dominio.Subasta;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RespositorioSubastaTest {

  private RepositorioSubasta repositorioSubasta;
  private SessionFactory sessionFactoryMock;
  private Session sessionMock;
  private Criteria criteriaMock;

  @BeforeEach
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);
    criteriaMock = mock(Criteria.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    when(sessionMock.createCriteria(Subasta.class)).thenReturn(criteriaMock);
    when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

    repositorioSubasta = new RepositorioSubastaImpl(sessionFactoryMock);
  }

  @Test
  public void guardarSubastaDeberiaLlamarAlMetodoSaveDeLaSesion() {
    Subasta subasta = new Subasta();

    repositorioSubasta.guardarSubasta(subasta);

    verify(sessionMock, times(1)).save(subasta);
  }

  @Test
  public void guardarSubastaDeberiaRetornarLaMismaSubasta() {
    Subasta subasta = new Subasta();

    Subasta resultado = repositorioSubasta.guardarSubasta(subasta);

    assertThat(resultado, equalTo(subasta));
  }

  @Test
  public void obtenerSubastaDeberiaRetornarSubastaSiExiste() {
    Subasta subasta = new Subasta();
    when(criteriaMock.uniqueResult()).thenReturn(subasta);

    Subasta subastaObtenida = repositorioSubasta.obtenerSubasta(1L);

    assertThat(subastaObtenida, equalTo(subasta));
  }

  @Test
  public void obtenerSubastaDeberiaRetornarNullSiNoExiste() {
    when(criteriaMock.uniqueResult()).thenReturn(null);

    Subasta subastaObtenida = repositorioSubasta.obtenerSubasta(1L);

    assertThat(subastaObtenida, equalTo(null));
  }
}
