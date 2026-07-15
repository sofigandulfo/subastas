package com.tallerwebi.dominio.usuario;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ServicioEmailImpl implements ServicioEmail {

  private final JavaMailSender mailSender;
  private static final Logger LOGGER = Logger.getLogger(ServicioEmailImpl.class.getName());

  @Autowired
  public ServicioEmailImpl(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void notificarGanador(
    String emailVendedor,
    String emailComprador,
    String nombreComprador
  ) {
    try {
      MimeMessage msgComprador = mailSender.createMimeMessage();
      MimeMessageHelper helperComprador = new MimeMessageHelper(msgComprador, true, "UTF-8");

      helperComprador.setFrom("sistema@subastapp.com");
      helperComprador.setTo(emailComprador);
      helperComprador.setSubject("¡Felicidades, ganaste la subasta!");

      String htmlComprador =
        "" +
        "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #f9f9f9;\">" +
        "  <h2 style=\"color: #27ae60; text-align: center;\">¡Subasta Finalizada!</h2>" +
        "  <p style=\"color: #333; font-size: 16px;\">Hola,</p>" +
        "  <p style=\"color: #333; font-size: 16px;\">Tenemos excelentes noticias. Tu oferta por la subasta fue la ganadora.</p>" +
        "  <div style=\"background-color: #ffffff; padding: 15px; border-left: 4px solid #27ae60; margin: 20px 0;\">" +
        "    <p style=\"margin: 0; color: #555;\"><strong>Contactá al vendedor a este email:</strong> " +
        emailVendedor +
        "</p>" +
        "  </div>" +
        "  <p style=\"color: #333; font-size: 16px;\">Por favor, escribile a la brevedad para coordinar el pago y la entrega.</p>" +
        "  <hr style=\"border: none; border-top: 1px solid #ddd; margin-top: 30px;\">" +
        "  <p style=\"color: #7f8c8d; font-size: 12px; text-align: center;\">Plataforma SubastAPP - Taller Web I</p>" +
        "</div>";

      helperComprador.setText(htmlComprador, true);
      mailSender.send(msgComprador);
    } catch (Exception e) {
      // En caso de que dos subastas terminen al mismo tiempo, por tener cuenta gratuita, se lanzaría una excepcion
      // En lugar de lanzar 'throw new RuntimeException', atrapamos la caída.
      // Si Mailtrap bloquea el envío, solo dejamos un registro y permitimos que la DB guarde los cambios.
      if (LOGGER.isLoggable(Level.WARNING)) {
        LOGGER.warning(
          "Aviso: No se pudo enviar el correo por limites de API externa. " + e.getMessage()
        );
      }
    }
  }
}
