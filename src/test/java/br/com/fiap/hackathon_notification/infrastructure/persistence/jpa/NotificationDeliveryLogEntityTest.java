package br.com.fiap.hackathon_notification.infrastructure.persistence.jpa;

import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationDeliveryLogEntityTest {

	@Test
	void shouldSetAndGetAllFields() {
		NotificationDeliveryLogEntity entity = new NotificationDeliveryLogEntity();

		UUID videoId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		Instant completedAt = Instant.parse("2026-03-13T20:30:00Z");
		Instant processedAt = Instant.parse("2026-03-13T20:31:00Z");

		entity.setId(1L);
		entity.setVideoId(videoId);
		entity.setUserId(userId);
		entity.setOriginalFilename("arquivo.mp4");
		entity.setS3ZipKey("s3/key.zip");
		entity.setCompletedAt(completedAt);
		entity.setEventType(VideoEventType.COMPLETED);
		entity.setRecipientEmail("destino@fiap.com");
		entity.setEmailSent(true);
		entity.setProviderMessageId("provider-123");
		entity.setErrorMessage("erro qualquer");
		entity.setProcessedAt(processedAt);
		entity.setAttemptCount(3);

		assertEquals(1L, entity.getId());
		assertEquals(videoId, entity.getVideoId());
		assertEquals(userId, entity.getUserId());
		assertEquals("arquivo.mp4", entity.getOriginalFilename());
		assertEquals("s3/key.zip", entity.getS3ZipKey());
		assertEquals(completedAt, entity.getCompletedAt());
		assertEquals(VideoEventType.COMPLETED, entity.getEventType());
		assertEquals("destino@fiap.com", entity.getRecipientEmail());
		assertTrue(entity.isEmailSent());
		assertEquals("provider-123", entity.getProviderMessageId());
		assertEquals("erro qualquer", entity.getErrorMessage());
		assertEquals(processedAt, entity.getProcessedAt());
		assertEquals(3, entity.getAttemptCount());

		entity.setEmailSent(false);
		assertFalse(entity.isEmailSent());
	}
}
