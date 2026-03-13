package br.com.fiap.hackathon_notification.domain.model;

import br.com.fiap.hackathon_notification.infrastructure.config.properties.EmailEngineProperties;
import br.com.fiap.hackathon_notification.infrastructure.email.emailengine.EmailEngineSubmitRequest;
import br.com.fiap.hackathon_notification.infrastructure.email.emailengine.EmailEngineSubmitResponse;
import br.com.fiap.hackathon_notification.infrastructure.messaging.rabbit.VideoEventMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainModelsTest {

	@Test
	void shouldCreateAndReadDomainRecords() {
		UUID videoId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		Instant completedAt = Instant.parse("2026-03-13T20:40:00Z");
		Instant processedAt = Instant.parse("2026-03-13T20:41:00Z");

		VideoProcessingEvent event = new VideoProcessingEvent(
				videoId,
				userId,
				"video.mp4",
				"s3/key.zip",
				completedAt,
				VideoEventType.COMPLETED);

		EmailNotificationResult result = new EmailNotificationResult(
				true,
				"destino@fiap.com",
				"provider-id",
				null,
				processedAt);

		NotificationDeliveryLog log = new NotificationDeliveryLog(
				videoId,
				userId,
				"video.mp4",
				"s3/key.zip",
				completedAt,
				VideoEventType.FAILED,
				"destino@fiap.com",
				false,
				null,
				"erro",
				processedAt,
				1);

		VideoEventMessage message = new VideoEventMessage(videoId, userId, "video.mp4", "s3/key.zip", completedAt);
		EmailEngineSubmitRequest request = new EmailEngineSubmitRequest(
				"origem@fiap.com",
				List.of("destino@fiap.com"),
				"assunto",
				"texto");
		EmailEngineSubmitResponse response = new EmailEngineSubmitResponse("message-id");
		EmailEngineProperties properties = new EmailEngineProperties("from@fiap.com", "fallback.com",
				"default@fiap.com");

		assertEquals(videoId, event.videoId());
		assertEquals(VideoEventType.COMPLETED, event.eventType());

		assertEquals(true, result.success());
		assertEquals("destino@fiap.com", result.recipientEmail());

		assertEquals(VideoEventType.FAILED, log.eventType());
		assertEquals("erro", log.errorMessage());

		assertEquals(videoId, message.videoId());
		assertEquals("video.mp4", message.originalFilename());

		assertEquals("origem@fiap.com", request.from());
		assertEquals("destino@fiap.com", request.to().get(0));
		assertEquals("message-id", response.messageId());

		assertEquals("from@fiap.com", properties.from());
		assertEquals("fallback.com", properties.toFallbackDomain());
		assertEquals("default@fiap.com", properties.defaultRecipient());
	}
}
