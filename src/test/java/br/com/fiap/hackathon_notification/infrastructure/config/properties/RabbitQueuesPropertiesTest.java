package br.com.fiap.hackathon_notification.infrastructure.config.properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RabbitQueuesPropertiesTest {

	@Test
	void shouldDeriveDeadLetterNamesAndRoutingKeys() {
		RabbitQueuesProperties properties = new RabbitQueuesProperties("video.completed", "video.failed");

		assertEquals("video.completed", properties.videoCompleted());
		assertEquals("video.failed", properties.videoFailed());
		assertEquals("video.completed.dlq", properties.videoCompletedDlq());
		assertEquals("video.failed.dlq", properties.videoFailedDlq());
		assertEquals("video.completed.dlq", properties.videoCompletedDlqRoutingKey());
		assertEquals("video.failed.dlq", properties.videoFailedDlqRoutingKey());
	}
}
