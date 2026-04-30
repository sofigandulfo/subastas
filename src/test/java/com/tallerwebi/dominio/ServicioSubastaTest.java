package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tallerwebi.dominio.excepcion.SubastaInvalidaExeption;

public class ServicioSubastaTest {

    private ServicioSubasta servicioSubasta;
    private RepositorioSubasta repositorioSubasta;

    @BeforeEach
    public void init() {
        repositorioSubasta = mock(RepositorioSubasta.class);
        servicioSubasta = new ServicioSubastaImpl(repositorioSubasta);
    }
    
    @Test
    public void queUnaSubastaTengaLosAtributosCorrectos() throws SubastaInvalidaExeption {
        Subasta subasta = new Subasta("Notebook", "Notebook 16gb", 1000.0, 5000.0, "Tecnologia", "nuevo");

        servicioSubasta.crearSubasta(subasta);

        assertEquals("Notebook", subasta.getNombre());
        assertEquals("Notebook 16gb", subasta.getDescripcion());
        assertEquals(1000.0, subasta.getPrecioInicial());
        assertEquals(5000.0, subasta.getPrecioMaximo());
        assertEquals("Tecnologia", subasta.getCategoria());
        assertEquals("nuevo", subasta.getEstado());
    }

    @Test
    public void queAlCrearUnaSubastaValidaSeGuardaEnLaBaseDeDatosCorrectamente() throws SubastaInvalidaExeption{
        Subasta subasta = new Subasta("Notebook", "Notebook 16gb", 1000.0, 5000.0, "Tecnologia", "nuevo");

        servicioSubasta.crearSubasta(subasta);

        verify(this.repositorioSubasta, times(1)).guardarSubasta(subasta);
    }

    @Test
    public void queAlCrearUnaSubastaValidaQuedaComoActiva() throws SubastaInvalidaExeption {
        Subasta subasta = new Subasta("Notebook", "Notebook 16gb", 1000.0, 5000.0, "Tecnologia", "nuevo");

        servicioSubasta.crearSubasta(subasta);

        String resultadoEsperado = "ACTIVA";
        String resultadoObtenido = subasta.getEstadoSubasta();
        assertEquals(resultadoEsperado, resultadoObtenido);
    }

    @Test
    public void queNoSePuedaCrearUnaSubastaSinNombre() {
        Subasta subasta = new Subasta("", "Notebook 16gb", 1000.0, 5000.0, "Tecnologia", "nuevo");


        assertThrows(SubastaInvalidaExeption.class, () -> servicioSubasta.crearSubasta(subasta));
        }

    @Test
    public void queNoSePuedaCrearUnaSubastaConPrecioInicialNegativo(){
        Subasta subasta = new Subasta("Notebook", "Notebook 16gb", -1000.0, 5000.0, "Tecnologia", "nuevo");

        assertThrows(SubastaInvalidaExeption.class, () -> servicioSubasta.crearSubasta(subasta));
    }

    @Test
    public void queNoSePuedaCrearUnaSubastaConPrecioMaximoNegativo(){
        Subasta subasta = new Subasta("Notebook", "Notebook 16gb", 1000.0, -5000.0, "Tecnologia", "nuevo");
    
        assertThrows(SubastaInvalidaExeption.class, () -> servicioSubasta.crearSubasta(subasta));
    }

    @Test
    public void queNoSePuedaCrearUnaSubastaConPrecioMaximoMenosAlPrecioInicial(){
        Subasta subasta = new Subasta("Notebook", "Notebook 16gb", 1000.0, 100.0, "Tecnologia", "nuevo");
    
        assertThrows(SubastaInvalidaExeption.class, () -> servicioSubasta.crearSubasta(subasta));
    }
}
