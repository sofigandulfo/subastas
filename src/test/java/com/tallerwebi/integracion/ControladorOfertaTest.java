package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tallerwebi.dominio.subasta.Subasta;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import java.util.Objects;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringWebTestConfig.class, HibernateTestConfig.class })
public class ControladorOfertaTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Autowired
  private SessionFactory sessionFactory;

  @BeforeEach
  public void init() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();

    Subasta subasta = new Subasta("Notebook", "Gamer", 1000.0, 10000.0, "Tecnologia", "Nuevo");
    session.save(subasta);

    tx.commit();
    session.close();
  }

  @AfterEach
  public void cleanup() {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    session.createQuery("DELETE FROM Subasta").executeUpdate();
    tx.commit();
    session.close();
  }

  @Test
  public void siElUsuarioNoTieneSesionYNavegaAOfertarDeberiaRedirigirALogin() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/ofertar/1")).andExpect(status().is3xxRedirection()).andReturn();
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siElUsuarioTieneSesionYQuiereOfertarDeberiaVerElFormulario() throws Exception {
    Session session = sessionFactory.openSession();
    Subasta subasta = session.createQuery("FROM Subasta", Subasta.class).getSingleResult();
    Long idReal = subasta.getId();
    session.close();

    MvcResult result =
      this.mockMvc.perform(get("/ofertar/" + idReal).sessionAttr("USUARIO_ID", 1L))
        .andExpect(status().isOk())
        .andReturn();

    assertThat(result.getModelAndView().getViewName(), equalToIgnoringCase("oferta"));
    assertThat(result.getModelAndView().getModel().get("subasta"), instanceOf(Subasta.class));
  }
}
