package co.edu.udistrital.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    //TODO Corregir y construir metodo asincronico
    public void enviarEmail(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("littlejumpers@noreply.com");
        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(mensaje);
        
        mailSender.send(email);
    }
}
