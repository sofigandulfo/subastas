package com.tallerwebi.infraestructura;

import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioOferta;
import com.tallerwebi.dominio.Oferta;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RespositorioOfertaTest {
    

    private RepositorioOferta repositorioOferta;
    private SessionFactory sessionFactoryMock;
    private Session sessionMock;

    @BeforeEach
    public void init(){
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);

        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

        repositorioOferta = new RepositorioOfertaImpl(sessionFactoryMock);
    }

    @Test
    public void guardarOfertaDeberiaLlamarAlMetodoSaveDeLaSesion(){
        Oferta oferta = new Oferta();

        repositorioOferta.guardarOferta(oferta);

        verify(sessionMock, times(1)).save(oferta);
    }
}
