package br.com.fiap.hackathon_notification.infrastructure.messaging.rabbit;

import br.com.fiap.hackathon_notification.application.port.in.ProcessVideoEventUseCase;
import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import br.com.fiap.hackathon_notification.domain.model.VideoProcessingEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VideoEventRabbitListenerTest {

	@Mock
	private ProcessVideoEventUseCase processVideoEventUseCase;

	@InjectMocks
	private VideoEventRabbitListener listener;

	@Test
	void shouldConvertCompletedMessageToDomainEvent() {
		VideoEventMessage message = new VideoEventMessage(
				UUID.randomUUID(),
				UUID.randomUUID(),
				"video-ok.mp4",
				"zip/key",
				Instant.parse("2026-03-13T20:15:00Z"));

		listener.onVideoCompleted(message);

		ArgumentCaptor<VideoProcessingEvent> captor = ArgumentCaptor.forClass(VideoProcessingEvent.class);
		verify(processVideoEventUseCase).process(captor.capture());

		VideoProcessingEvent event = captor.getValue();
		assertEquals(message.videoId(), event.videoId());
		assertEquals(message.userId(), event.userId());
		assertEquals(message.originalFilename(), event.originalFilename());
		assertEquals(message.s3ZipKey(), event.s3ZipKey());
		assertEquals(message.completedAt(), event.completedAt());
		assertEquals(VideoEventType.COMPLETED, event.eventType());
	}

	@Test
	void shouldConvertFailedMessageToDomainEvent() {
		VideoEventMessage message = new VideoEventMessage(
				UUID.randomUUID(),
				UUID.randomUUID(),
				"video-fail.mp4",
				"zip/key",
				Instant.parse("2026-03-13T20:16:00Z"));

		listener.onVideoFailed(message);

		ArgumentCaptor<VideoProcessingEvent> captor = ArgumentCaptor.forClass(VideoProcessingEvent.class);
		verify(processVideoEventUseCase).process(captor.capture());

		VideoProcessingEvent event = captor.getValue();
		assertEquals(VideoEventType.FAILED, event.eventType());
		assertEquals(message.videoId(), event.videoId());
	}
}
