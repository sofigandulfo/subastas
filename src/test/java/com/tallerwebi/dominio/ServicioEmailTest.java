package com.tallerwebi.dominio;

import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.usuario.ServicioEmailImpl;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

public class ServicioEmailTest {

  private JavaMailSender mailSenderMock;
  private ServicioEmailImpl servicioEmail;
  private MimeMessage mimeMessageMock;

  @BeforeEach
  public void init() {
    // Simulamos las herramientas de Spring Mail
    mailSenderMock = mock(JavaMailSender.class);
    mimeMessageMock = mock(MimeMessage.class);

    // Cuando el servicio pida un mensaje vacío, le damos el simulacro
    when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);

    servicioEmail = new ServicioEmailImpl(mailSenderMock);
  }

  @Test
  public void queSePuedaEnviarUnCorreoDeNotificacionAlGanador() {
    servicioEmail.notificarGanador("vendedor@test.com", "comprador@test.com", "Pablo");

    // Validación: Verificamos que el mailSender efectivamente haya mandado el mail
    verify(mailSenderMock, times(1)).send(mimeMessageMock);
  }
}
