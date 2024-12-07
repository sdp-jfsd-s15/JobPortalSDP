package com.klef.JobPortal.Controller;
import com.klef.JobPortal.dtos.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/mail")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send-email")
    public String sendEmail(@RequestBody EmailRequest emailRequest) {
        try {
            // App Password: efjo krha fnha xrol
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(emailRequest.getSubject());
            helper.setTo(emailRequest.getRecipients().toArray(new String[0]));
            helper.setText(emailRequest.getBody(), true); // Set to true for HTML content

            mailSender.send(message);
            return "Email sent successfully!";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send email.";
        }
    }
}
