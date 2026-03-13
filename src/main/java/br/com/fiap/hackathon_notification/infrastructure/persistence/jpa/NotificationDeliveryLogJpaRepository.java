package br.com.fiap.hackathon_notification.infrastructure.persistence.jpa;

import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationDeliveryLogJpaRepository extends JpaRepository<NotificationDeliveryLogEntity, Long> {

	Optional<NotificationDeliveryLogEntity> findByVideoIdAndEventType(UUID videoId, VideoEventType eventType);
}
