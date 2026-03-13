package br.com.fiap.hackathon_notification.infrastructure.email.javamail;

import br.com.fiap.hackathon_notification.application.port.out.EmailNotificationPort;
import br.com.fiap.hackathon_notification.domain.model.EmailNotificationResult;
import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import br.com.fiap.hackathon_notification.domain.model.VideoProcessingEvent;
import br.com.fiap.hackathon_notification.infrastructure.config.properties.EmailEngineProperties;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JavaMailEmailNotificationAdapter implements EmailNotificationPort {

	private static final Logger log = LoggerFactory.getLogger(JavaMailEmailNotificationAdapter.class);

	private final JavaMailSender mailSender;
	private final EmailEngineProperties emailProperties;

	public JavaMailEmailNotificationAdapter(JavaMailSender mailSender, EmailEngineProperties emailProperties) {
		this.mailSender = mailSender;
		this.emailProperties = emailProperties;
	}

	@Override
	public EmailNotificationResult send(VideoProcessingEvent event, String recipientEmail) {
		log.info("[Mail] Enviando email para {} (evento {})", recipientEmail, event.eventType());
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
			helper.setFrom(emailProperties.from());
			helper.setTo(recipientEmail);
			helper.setSubject(buildSubject(event));
			helper.setText(buildBody(event), false);
			mailSender.send(message);
			log.info("[Mail] Email enviado com sucesso para {}", recipientEmail);
			return new EmailNotificationResult(true, recipientEmail, null, null, Instant.now());
		} catch (Exception e) {
			log.error("[Mail] Erro ao enviar email para {}: {}", recipientEmail, e.getMessage());
			return new EmailNotificationResult(false, recipientEmail, null, e.getMessage(), Instant.now());
		}
	}

	private String buildSubject(VideoProcessingEvent event) {
		return event.eventType() == VideoEventType.COMPLETED
				? "Seu vídeo foi processado com sucesso"
				: "Falha no processamento do seu vídeo";
	}

	private String buildBody(VideoProcessingEvent event) {
		String template = event.eventType() == VideoEventType.COMPLETED
				? "Seu vídeo \"%s\" foi processado com sucesso. ID do vídeo: %s."
				: "Não foi possível processar o vídeo \"%s\". ID do vídeo: %s.";
		return template.formatted(event.originalFilename(), event.videoId());
	}

}
