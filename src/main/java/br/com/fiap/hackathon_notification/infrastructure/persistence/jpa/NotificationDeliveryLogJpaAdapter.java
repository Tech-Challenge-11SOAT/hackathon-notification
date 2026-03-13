package br.com.fiap.hackathon_notification.infrastructure.persistence.jpa;

import br.com.fiap.hackathon_notification.application.port.out.NotificationDeliveryLogPort;
import br.com.fiap.hackathon_notification.domain.model.NotificationDeliveryLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationDeliveryLogJpaAdapter implements NotificationDeliveryLogPort {

	private static final Logger log = LoggerFactory.getLogger(NotificationDeliveryLogJpaAdapter.class);

	private final NotificationDeliveryLogJpaRepository repository;

	public NotificationDeliveryLogJpaAdapter(NotificationDeliveryLogJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public void save(NotificationDeliveryLog deliveryLog) {
		log.info("[DB] Salvando log de entrega para videoId={}, evento={}", deliveryLog.videoId(),
				deliveryLog.eventType());
		NotificationDeliveryLogEntity entity = repository
				.findByVideoIdAndEventType(deliveryLog.videoId(), deliveryLog.eventType())
				.orElseGet(NotificationDeliveryLogEntity::new);

		if (entity.getId() == null) {
			entity.setVideoId(deliveryLog.videoId());
			entity.setEventType(deliveryLog.eventType());
		}

		entity.setUserId(deliveryLog.userId());
		entity.setOriginalFilename(deliveryLog.originalFilename());
		entity.setS3ZipKey(deliveryLog.s3ZipKey());
		entity.setCompletedAt(deliveryLog.completedAt());
		entity.setRecipientEmail(deliveryLog.recipientEmail());
		entity.setEmailSent(deliveryLog.emailSent());
		entity.setProviderMessageId(deliveryLog.providerMessageId());
		entity.setErrorMessage(deliveryLog.errorMessage());
		entity.setProcessedAt(deliveryLog.processedAt());
		boolean isNew = entity.getId() == null;
		entity.setAttemptCount(isNew ? deliveryLog.attemptCount() : entity.getAttemptCount() + 1);

		repository.save(entity);
		log.info("[DB] Log {} com sucesso (tentativa {}) para videoId={}",
				isNew ? "inserido" : "atualizado", entity.getAttemptCount(), deliveryLog.videoId());
	}
}
