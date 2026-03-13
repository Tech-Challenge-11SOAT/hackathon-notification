package br.com.fiap.hackathon_notification.application.port.in;

import br.com.fiap.hackathon_notification.domain.model.VideoProcessingEvent;

public interface ProcessVideoEventUseCase {

	void process(VideoProcessingEvent event);
}
