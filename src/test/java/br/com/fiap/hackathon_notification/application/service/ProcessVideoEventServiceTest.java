package br.com.fiap.hackathon_notification.application.service;

import br.com.fiap.hackathon_notification.application.port.out.EmailNotificationPort;
import br.com.fiap.hackathon_notification.application.port.out.NotificationDeliveryLogPort;
import br.com.fiap.hackathon_notification.domain.model.EmailNotificationResult;
import br.com.fiap.hackathon_notification.domain.model.NotificationDeliveryLog;
import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import br.com.fiap.hackathon_notification.domain.model.VideoProcessingEvent;
import br.com.fiap.hackathon_notification.infrastructure.config.properties.EmailEngineProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessVideoEventServiceTest {

	@Mock
	private EmailNotificationPort emailNotificationPort;

	@Mock
	private NotificationDeliveryLogPort notificationDeliveryLogPort;

	@Test
	void shouldProcessSuccessfullyUsingDefaultRecipient() {
		EmailEngineProperties properties = new EmailEngineProperties("from@fiap.com", "fallback.com",
				"destino@fiap.com");
		ProcessVideoEventService service = new ProcessVideoEventService(
				emailNotificationPort,
				notificationDeliveryLogPort,
				properties);

		Instant now = Instant.parse("2026-03-13T20:00:00Z");
		VideoProcessingEvent event = new VideoProcessingEvent(
				UUID.randomUUID(),
				UUID.randomUUID(),
				"video.mp4",
				"s3/key.zip",
				now,
				VideoEventType.COMPLETED);

		EmailNotificationResult result = new EmailNotificationResult(true, "destino@fiap.com", "msg-1", null, now);
		when(emailNotificationPort.send(eq(event), eq("destino@fiap.com"))).thenReturn(result);

		service.process(event);

		verify(emailNotificationPort).send(event, "destino@fiap.com");
		ArgumentCaptor<NotificationDeliveryLog> captor = ArgumentCaptor.forClass(NotificationDeliveryLog.class);
		verify(notificationDeliveryLogPort).save(captor.capture());

		NotificationDeliveryLog savedLog = captor.getValue();
		assertEquals(event.videoId(), savedLog.videoId());
		assertEquals(event.eventType(), savedLog.eventType());
		assertEquals("destino@fiap.com", savedLog.recipientEmail());
		assertEquals(1, savedLog.attemptCount());
		assertEquals(now, savedLog.processedAt());
	}

	@Test
	void shouldUseFallbackDomainWhenDefaultRecipientIsBlank() {
		EmailEngineProperties properties = new EmailEngineProperties("from@fiap.com", "fallback.com", "   ");
		ProcessVideoEventService service = new ProcessVideoEventService(
				emailNotificationPort,
				notificationDeliveryLogPort,
				properties);

		UUID userId = UUID.randomUUID();
		VideoProcessingEvent event = new VideoProcessingEvent(
				UUID.randomUUID(),
				userId,
				"video.mp4",
				"s3/key.zip",
				Instant.now(),
				VideoEventType.FAILED);

		String expectedRecipient = userId + "@fallback.com";
		when(emailNotificationPort.send(eq(event), eq(expectedRecipient)))
				.thenReturn(new EmailNotificationResult(true, expectedRecipient, null, null, Instant.now()));

		service.process(event);

		verify(emailNotificationPort).send(event, expectedRecipient);
	}

	@Test
	void shouldSaveLogAndThrowWhenEmailFails() {
		EmailEngineProperties properties = new EmailEngineProperties("from@fiap.com", "fallback.com", null);
		ProcessVideoEventService service = new ProcessVideoEventService(
				emailNotificationPort,
				notificationDeliveryLogPort,
				properties);

		UUID userId = UUID.randomUUID();
		VideoProcessingEvent event = new VideoProcessingEvent(
				UUID.randomUUID(),
				userId,
				"video.mp4",
				"s3/key.zip",
				Instant.now(),
				VideoEventType.FAILED);

		String expectedRecipient = userId + "@fallback.com";
		when(emailNotificationPort.send(eq(event), eq(expectedRecipient)))
				.thenReturn(new EmailNotificationResult(false, expectedRecipient, null, "smtp error", null));

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.process(event));
		assertEquals("Falha ao enviar notificação por email para o evento FAILED", exception.getMessage());

		ArgumentCaptor<NotificationDeliveryLog> captor = ArgumentCaptor.forClass(NotificationDeliveryLog.class);
		verify(notificationDeliveryLogPort).save(captor.capture());
		NotificationDeliveryLog savedLog = captor.getValue();
		assertEquals(expectedRecipient, savedLog.recipientEmail());
		assertEquals("smtp error", savedLog.errorMessage());
		assertNotNull(savedLog.processedAt());
	}
}
