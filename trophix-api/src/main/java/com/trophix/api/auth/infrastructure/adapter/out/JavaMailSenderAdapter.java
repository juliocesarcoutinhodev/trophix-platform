package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.application.ports.out.EmailSenderPort;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Year;
import java.util.Map;

/**
 * Sends the password reset e-mail through the configured SMTP server
 * (Mailpit in development) using the HTML template in
 * {@code templates/email/password-reset.html}. Runs asynchronously so the HTTP
 * request returns immediately.
 */
@Component
@Slf4j
public class JavaMailSenderAdapter implements EmailSenderPort {

    private static final String TEMPLATE_PATH = "templates/email/password-reset.html";
    private static final String PLAIN_TEXT = """
            Olá, %s!

            Recebemos uma solicitação para redefinir a senha da sua conta TROPHIX.

            Acesse o link abaixo para definir uma nova senha (válido por %s, uso único):
            %s

            Se você não solicitou a redefinição, ignore este e-mail.
            """;

    private final JavaMailSender mailSender;
    private final String from;
    private final Duration tokenTtl;
    private final String template;

    public JavaMailSenderAdapter(JavaMailSender mailSender,
                                 @Value("${trophix.mail.from}") String from,
                                 @Value("${trophix.password-reset.token-ttl:PT1H}") Duration tokenTtl) {
        this.mailSender = mailSender;
        this.from = from;
        this.tokenTtl = tokenTtl;
        this.template = loadTemplate();
    }

    @Async
    @Override
    public void sendPasswordReset(String to, String username, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Redefinição de senha — TROPHIX");
            helper.setText(renderPlainText(username, resetUrl), renderHtml(username, resetUrl));
            mailSender.send(message);
            log.info("Email de redefinição de senha enviado para {}", to);
        } catch (Exception e) {
            log.error("Falha ao enviar email de redefinição de senha para {}", to, e);
        }
    }

    private String renderHtml(String username, String resetUrl) {
        Map<String, String> vars = Map.of(
                "USERNAME", username,
                "RESET_URL", resetUrl,
                "EXPIRES_IN", formatDuration(tokenTtl),
                "YEAR", String.valueOf(Year.now().getValue()));
        String html = template;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return html;
    }

    private String renderPlainText(String username, String resetUrl) {
        return PLAIN_TEXT.formatted(username, formatDuration(tokenTtl), resetUrl);
    }

    private String loadTemplate() {
        try {
            return StreamUtils.copyToString(
                    new ClassPathResource(TEMPLATE_PATH).getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Template de email não encontrado: " + TEMPLATE_PATH, e);
        }
    }

    private String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        if (hours >= 1 && duration.minus(Duration.ofHours(hours)).toMinutes() == 0) {
            return hours == 1 ? "1 hora" : hours + " horas";
        }
        return minutes + " minutos";
    }
}
