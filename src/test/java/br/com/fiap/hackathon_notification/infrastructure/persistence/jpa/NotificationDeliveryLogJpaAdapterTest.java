package br.com.fiap.hackathon_notification.infrastructure.persistence.jpa;

import br.com.fiap.hackathon_notification.domain.model.NotificationDeliveryLog;
import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryLogJpaAdapterTest {

	@Mock
	private NotificationDeliveryLogJpaRepository repository;

	@InjectMocks
	private NotificationDeliveryLogJpaAdapter adapter;

	@Test
	void shouldInsertNewEntityWithAttemptCountFromDomain() {
		NotificationDeliveryLog log = buildLog(VideoEventType.COMPLETED, 1);
		when(repository.findByVideoIdAndEventType(log.videoId(), log.eventType())).thenReturn(Optional.empty());

		adapter.save(log);

		ArgumentCaptor<NotificationDeliveryLogEntity> captor = ArgumentCaptor
				.forClass(NotificationDeliveryLogEntity.class);
		verify(repository).save(captor.capture());
		NotificationDeliveryLogEntity saved = captor.getValue();

		assertEquals(log.videoId(), saved.getVideoId());
		assertEquals(log.eventType(), saved.getEventType());
		assertEquals(log.userId(), saved.getUserId());
		assertEquals(log.originalFilename(), saved.getOriginalFilename());
		assertEquals(log.s3ZipKey(), saved.getS3ZipKey());
		assertEquals(log.completedAt(), saved.getCompletedAt());
		assertEquals(log.recipientEmail(), saved.getRecipientEmail());
		assertEquals(log.emailSent(), saved.isEmailSent());
		assertEquals(log.providerMessageId(), saved.getProviderMessageId());
		assertEquals(log.errorMessage(), saved.getErrorMessage());
		assertEquals(log.processedAt(), saved.getProcessedAt());
		assertEquals(1, saved.getAttemptCount());
	}

	@Test
	void shouldUpdateExistingEntityAndIncrementAttemptCount() {
		NotificationDeliveryLog log = buildLog(VideoEventType.FAILED, 1);
		NotificationDeliveryLogEntity existing = new NotificationDeliveryLogEntity();
		existing.setId(10L);
		existing.setVideoId(log.videoId());
		existing.setEventType(log.eventType());
		existing.setAttemptCount(2);

		when(repository.findByVideoIdAndEventType(log.videoId(), log.eventType())).thenReturn(Optional.of(existing));

		adapter.save(log);

		ArgumentCaptor<NotificationDeliveryLogEntity> captor = ArgumentCaptor
				.forClass(NotificationDeliveryLogEntity.class);
		verify(repository).save(captor.capture());
		NotificationDeliveryLogEntity saved = captor.getValue();

		assertEquals(10L, saved.getId());
		assertEquals(3, saved.getAttemptCount());
		assertEquals(log.userId(), saved.getUserId());
		assertEquals(log.errorMessage(), saved.getErrorMessage());
		assertEquals(log.recipientEmail(), saved.getRecipientEmail());
	}

	private NotificationDeliveryLog buildLog(VideoEventType eventType, int attemptCount) {
		return new NotificationDeliveryLog(
				UUID.randomUUID(),
				UUID.randomUUID(),
				"video.mp4",
				"s3/key.zip",
				Instant.parse("2026-03-13T20:20:00Z"),
				eventType,
				"destino@fiap.com",
				eventType == VideoEventType.COMPLETED,
				"provider-1",
				eventType == VideoEventType.FAILED ? "erro" : null,
				Instant.parse("2026-03-13T20:21:00Z"),
				attemptCount);
	}
}
