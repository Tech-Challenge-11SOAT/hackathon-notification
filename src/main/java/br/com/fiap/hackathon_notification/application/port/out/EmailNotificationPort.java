package br.com.fiap.hackathon_notification.application.port.out;

import br.com.fiap.hackathon_notification.domain.model.EmailNotificationResult;
import br.com.fiap.hackathon_notification.domain.model.VideoProcessingEvent;

public interface EmailNotificationPort {

	EmailNotificationResult send(VideoProcessingEvent event, String recipientEmail);
}
