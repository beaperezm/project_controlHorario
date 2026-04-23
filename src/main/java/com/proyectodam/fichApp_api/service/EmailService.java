package com.proyectodam.fichApp_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendNominaNotification(String toEmail, String monthYear) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Nueva Nómina Disponible: " + monthYear);
            message.setText("Hola,\n\nTienes una nueva nómina (" + monthYear
                    + ") disponible en Módulo de Documentos de Fich-App.\n" +
                    "Por favor, inicia sesión en la aplicación para revisarla y firmarla digitalmente.\n\n" +
                    "Un saludo,\nEquipo de Administración");
            mailSender.send(message);
            log.info("Email de notificación de nómina enviado a {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar email de nómina a {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendEstadoVacacionesEmail(String toEmail, String periodo, String estado, String comentario) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Actualización del estado de tu solicitud de vacaciones");
            
            StringBuilder body = new StringBuilder();
            body.append("Las vacaciones ").append(periodo).append(" han sido ").append(estado).append(".\n");
            
            if (comentario != null && !comentario.trim().isEmpty()) {
                body.append("Comentario del gestor: ").append(comentario).append("\n");
            }
            
            body.append("Fecha: ").append(java.time.LocalDate.now().toString()).append("\n\n");
            body.append("Un saludo,\nEquipo de Administración");
            
            message.setText(body.toString());
            mailSender.send(message);
            log.info("Email de estado de vacaciones enviado a {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", toEmail, e.getMessage());
        }
    }
}
