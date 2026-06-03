package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import java.util.Objects;
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
public class ControladorSubastaTest {

  @Autowired
  private WebApplicationContext wac; //contexto web

  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build(); //springMVC
  }

  @Test
  public void siElUsuarioNoTieneSesionYEntraACrearSubastaDeberiaRedirigirALogin() throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/crear-subasta"))
        .andExpect(status().is3xxRedirection())
        .andReturn(); //simula get crear subasta, espera redirect
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siElUsuarioTieneSesionYEntraACrearSubastaDeberiaRedirigirACrearSubasta()
    throws Exception {
    MvcResult result =
      this.mockMvc.perform(get("/crear-subasta").sessionAttr("USUARIO_ID", 1L))
        .andExpect(status().isOk())
        .andReturn(); //simula get crear subasta con usuario logueado, espera http 200
    ModelAndView modelAndView = result.getModelAndView();
    assert modelAndView != null;
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("crear-subasta"));
  }
}
