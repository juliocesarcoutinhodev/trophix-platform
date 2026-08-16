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
import java.util.UUID;

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
    private static final String REPLY_TEMPLATE_PATH = "templates/email/reply-notification.html";
    private static final String PLAIN_TEXT = """
            Olá, %s!

            Recebemos uma solicitação para redefinir a senha da sua conta TROPHIX.

            Acesse o link abaixo para definir uma nova senha (válido por %s, uso único):
            %s

            Se você não solicitou a redefinição, ignore este e-mail.
            """;

    private static final String REPLY_PLAIN_TEXT = """
            Olá, %s!

            %s respondeu ao seu tópico "%s" no fórum da TROPHIX.

            Acesse para ver a resposta:
            %s
            """;

    private final JavaMailSender mailSender;
    private final String from;
    private final Duration tokenTtl;
    private final String forumFrontendUrl;
    private final String template;
    private final String replyTemplate;

    public JavaMailSenderAdapter(JavaMailSender mailSender,
                                 @Value("${trophix.mail.from}") String from,
                                 @Value("${trophix.password-reset.token-ttl:PT1H}") Duration tokenTtl,
                                 @Value("${trophix.forums.frontend-url:http://localhost:4200}") String forumFrontendUrl) {
        this.mailSender = mailSender;
        this.from = from;
        this.tokenTtl = tokenTtl;
        this.forumFrontendUrl = forumFrontendUrl;
        this.template = loadTemplate(TEMPLATE_PATH);
        this.replyTemplate = loadTemplate(REPLY_TEMPLATE_PATH);
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

    @Async
    @Override
    public void sendReplyNotification(String to, String topicAuthorUsername,
                                      String replyAuthorUsername, String topicTitle, UUID topicId) {
        try {
            String topicUrl = forumFrontendUrl + "/forums/topics/" + topicId;
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Nova resposta no seu tópico — TROPHIX");
            helper.setText(renderReplyPlainText(topicAuthorUsername, replyAuthorUsername, topicTitle, topicUrl),
                    renderReplyHtml(topicAuthorUsername, replyAuthorUsername, topicTitle, topicUrl));
            mailSender.send(message);
            log.info("Email de nova resposta enviado para {}", to);
        } catch (Exception e) {
            log.error("Falha ao enviar email de nova resposta para {}", to, e);
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

    private String renderReplyPlainText(String topicAuthorUsername, String replyAuthorUsername,
                                        String topicTitle, String topicUrl) {
        return REPLY_PLAIN_TEXT.formatted(topicAuthorUsername, replyAuthorUsername, topicTitle, topicUrl);
    }

    private String renderReplyHtml(String topicAuthorUsername, String replyAuthorUsername,
                                   String topicTitle, String topicUrl) {
        Map<String, String> vars = Map.of(
                "USERNAME", topicAuthorUsername,
                "REPLY_AUTHOR", replyAuthorUsername,
                "TOPIC_TITLE", topicTitle,
                "TOPIC_URL", topicUrl,
                "YEAR", String.valueOf(Year.now().getValue()));
        String html = replyTemplate;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return html;
    }

    private String loadTemplate(String path) {
        try {
            return StreamUtils.copyToString(
                    new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Template de email não encontrado: " + path, e);
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
