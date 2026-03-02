package com.ait.shop.service;

import com.ait.shop.domain.User;
import com.ait.shop.exceptions.types.EmailSendingException;
import com.ait.shop.service.interfaces.ConfirmationCodeService;
import com.ait.shop.service.interfaces.EmailService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final Configuration mailConfig;
    private final ConfirmationCodeService confirmationCodeService;
    private final String host;
    private final String port;
    private final String mailFrom;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            Configuration mailConfig,
            ConfirmationCodeService confirmationCodeService,
            @Value("${server.host}") String host,
            @Value("${server.port}") String port,
            @Value("${spring.mail.username}") String mailFrom
    ) {
        this.mailSender = mailSender;
        this.mailConfig = mailConfig;
        this.confirmationCodeService = confirmationCodeService;
        this.host = host;
        this.port = port;
        this.mailFrom = mailFrom;

        mailConfig.setDefaultEncoding("UTF-8");
        TemplateLoader loader = new ClassTemplateLoader(EmailServiceImpl.class, "/mail/");
        mailConfig.setTemplateLoader(loader);
    }

    @Override
    public void sendConfirmationEmail(User user) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        String text = generateConfirmationEmail(user);

        try {
            helper.setFrom(mailFrom);
            helper.setTo(user.getEmail());
            helper.setSubject("Registration confirmation");
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Email Sending error", e);
        }

    }

    private String generateConfirmationEmail(User user) {
        try {
            Template template = mailConfig.getTemplate("confirm_registration_mail.ftlh");
            String code = confirmationCodeService.generateConfirmationCode(user);

            //http://localhost:8080/users/confirm/kbkjgi7897h-oyt0-979yi
            //При клике на эту ссылку, браузер будет отправлять GET запрос на наш бэкэнд
            String link = String.format("http://%s:%s/users/confirm/%s", host, port, code);

            Map<String, Object> mailValues = new HashMap<>();
            mailValues.put("name", user.getName());
            mailValues.put("link", link);

            return FreeMarkerTemplateUtils.processTemplateIntoString(template, mailValues);
        } catch (IOException | TemplateException e) {
            throw new EmailSendingException("Email text generation error", e);
        }
    }
}
