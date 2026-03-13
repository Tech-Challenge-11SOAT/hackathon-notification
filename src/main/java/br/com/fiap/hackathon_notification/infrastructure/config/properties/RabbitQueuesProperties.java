package br.com.fiap.hackathon_notification.infrastructure.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbit.queues")
public record RabbitQueuesProperties(
		@NotBlank String videoCompleted,
		@NotBlank String videoFailed) {

	public String videoCompletedDlq() {
		return videoCompleted + ".dlq";
	}

	public String videoFailedDlq() {
		return videoFailed + ".dlq";
	}

	public String videoCompletedDlqRoutingKey() {
		return videoCompletedDlq();
	}

	public String videoFailedDlqRoutingKey() {
		return videoFailedDlq();
	}
}
