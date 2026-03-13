package br.com.fiap.hackathon_notification.infrastructure.messaging.rabbit;

import br.com.fiap.hackathon_notification.application.port.in.ProcessVideoEventUseCase;
import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import br.com.fiap.hackathon_notification.domain.model.VideoProcessingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class VideoEventRabbitListener {

	private static final Logger log = LoggerFactory.getLogger(VideoEventRabbitListener.class);

	private final ProcessVideoEventUseCase processVideoEventUseCase;

	public VideoEventRabbitListener(ProcessVideoEventUseCase processVideoEventUseCase) {
		this.processVideoEventUseCase = processVideoEventUseCase;
	}

	@RabbitListener(queues = "${app.rabbit.queues.video-completed}")
	public void onVideoCompleted(VideoEventMessage message) {
		log.info("[RabbitMQ] Mensagem recebida na fila video-completed: videoId={}, userId={}, arquivo={}",
				message.videoId(), message.userId(), message.originalFilename());
		processVideoEventUseCase.process(toDomainEvent(message, VideoEventType.COMPLETED));
	}

	@RabbitListener(queues = "${app.rabbit.queues.video-failed}")
	public void onVideoFailed(VideoEventMessage message) {
		log.info("[RabbitMQ] Mensagem recebida na fila video-failed: videoId={}, userId={}, arquivo={}",
				message.videoId(), message.userId(), message.originalFilename());
		processVideoEventUseCase.process(toDomainEvent(message, VideoEventType.FAILED));
	}

	private VideoProcessingEvent toDomainEvent(VideoEventMessage message, VideoEventType eventType) {
		return new VideoProcessingEvent(
				message.videoId(),
				message.userId(),
				message.originalFilename(),
				message.s3ZipKey(),
				message.completedAt(),
				eventType);
	}
}
