package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.LoginGoogleException;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.usuario.ServicioGoogleOAuth2Impl;
import com.tallerwebi.dominio.usuario.ServicioLogin;
import com.tallerwebi.dominio.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class ServicioGoogleOAuth2ImplTest {

  private ServicioLogin servicioLoginMock;
  private OidcUserService delegateMock;
  private ServicioGoogleOAuth2Impl servicioGoogleOAuth2;
  private OidcUserRequest requestMock;
  private OidcUser oidcUserMock;

  @BeforeEach
  public void init() {
    servicioLoginMock = mock(ServicioLogin.class);
    delegateMock = mock(OidcUserService.class);
    servicioGoogleOAuth2 = new ServicioGoogleOAuth2Impl(servicioLoginMock, delegateMock);
    requestMock = mock(OidcUserRequest.class);
    oidcUserMock = mock(OidcUser.class);
  }

  @Test
  public void siElUsuarioYaExisteNoLoVuelveARegistrar() throws UsuarioExistente {
    when(delegateMock.loadUser(requestMock)).thenReturn(oidcUserMock);
    when(oidcUserMock.getAttribute("email")).thenReturn("dami@unlam.com");
    when(servicioLoginMock.buscar("dami@unlam.com")).thenReturn(new Usuario());

    OidcUser resultado = servicioGoogleOAuth2.loadUser(requestMock);

    assertThat(resultado, equalTo(oidcUserMock));
    verify(servicioLoginMock, never()).registrar(any());
  }

  @Test
  public void siElUsuarioNoExisteLoRegistraAutomaticamente() throws UsuarioExistente {
    when(delegateMock.loadUser(requestMock)).thenReturn(oidcUserMock);
    when(oidcUserMock.getAttribute("email")).thenReturn("nuevo@unlam.com");
    when(servicioLoginMock.buscar("nuevo@unlam.com")).thenReturn(null);

    OidcUser resultado = servicioGoogleOAuth2.loadUser(requestMock);

    assertThat(resultado, equalTo(oidcUserMock));
    verify(servicioLoginMock, times(1)).registrar(any(Usuario.class));
  }

  @Test
  public void siElRegistroFallaPorUsuarioExistenteLanzaLoginGoogleException()
    throws UsuarioExistente {
    when(delegateMock.loadUser(requestMock)).thenReturn(oidcUserMock);
    when(oidcUserMock.getAttribute("email")).thenReturn("nuevo@unlam.com");
    when(servicioLoginMock.buscar("nuevo@unlam.com")).thenReturn(null);
    doThrow(UsuarioExistente.class).when(servicioLoginMock).registrar(any(Usuario.class));

    org.junit.jupiter.api.Assertions.assertThrows(
      LoginGoogleException.class,
      () -> servicioGoogleOAuth2.loadUser(requestMock)
    );
  }
}
