package br.com.fiap.hackathon_notification.application.service;

import br.com.fiap.hackathon_notification.application.port.in.ProcessVideoEventUseCase;
import br.com.fiap.hackathon_notification.application.port.out.EmailNotificationPort;
import br.com.fiap.hackathon_notification.application.port.out.NotificationDeliveryLogPort;
import br.com.fiap.hackathon_notification.domain.model.EmailNotificationResult;
import br.com.fiap.hackathon_notification.domain.model.NotificationDeliveryLog;
import br.com.fiap.hackathon_notification.domain.model.VideoProcessingEvent;
import br.com.fiap.hackathon_notification.infrastructure.config.properties.EmailEngineProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ProcessVideoEventService implements ProcessVideoEventUseCase {

	private static final Logger log = LoggerFactory.getLogger(ProcessVideoEventService.class);

	private final EmailNotificationPort emailNotificationPort;
	private final NotificationDeliveryLogPort notificationDeliveryLogPort;
	private final EmailEngineProperties emailEngineProperties;

	public ProcessVideoEventService(
			EmailNotificationPort emailNotificationPort,
			NotificationDeliveryLogPort notificationDeliveryLogPort,
			EmailEngineProperties emailEngineProperties) {
		this.emailNotificationPort = emailNotificationPort;
		this.notificationDeliveryLogPort = notificationDeliveryLogPort;
		this.emailEngineProperties = emailEngineProperties;
	}

	@Override
	public void process(VideoProcessingEvent event) {
		log.info("[Service] Processando evento {} para videoId={}", event.eventType(), event.videoId());

		String recipientEmail = resolveRecipientEmail(event);
		log.info("[Service] Destinatário resolvido: {}", recipientEmail);

		EmailNotificationResult emailResult = emailNotificationPort.send(event, recipientEmail);

		if (emailResult.success()) {
			log.info("[Service] Email enviado com sucesso para {} (evento {})", recipientEmail, event.eventType());
		} else {
			log.warn("[Service] Falha ao enviar email para {} (evento {}): {}",
					recipientEmail, event.eventType(), emailResult.errorMessage());
		}

		NotificationDeliveryLog deliveryLog = new NotificationDeliveryLog(
				event.videoId(),
				event.userId(),
				event.originalFilename(),
				event.s3ZipKey(),
				event.completedAt(),
				event.eventType(),
				emailResult.recipientEmail(),
				emailResult.success(),
				emailResult.providerMessageId(),
				emailResult.errorMessage(),
				emailResult.processedAt() != null ? emailResult.processedAt() : Instant.now(),
				1);

		notificationDeliveryLogPort.save(deliveryLog);
		log.info("[Service] Log de entrega salvo no banco para videoId={}", event.videoId());

		if (!emailResult.success()) {
			throw new IllegalStateException("Falha ao enviar notificação por email para o evento " + event.eventType());
		}

		log.info("[Service] Evento {} processado com sucesso para videoId={}", event.eventType(), event.videoId());
	}

	private String resolveRecipientEmail(VideoProcessingEvent event) {
		if (emailEngineProperties.defaultRecipient() != null && !emailEngineProperties.defaultRecipient().isBlank()) {
			return emailEngineProperties.defaultRecipient();
		}

		return event.userId() + "@" + emailEngineProperties.toFallbackDomain();
	}
}
