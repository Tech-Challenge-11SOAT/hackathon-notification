package br.com.fiap.hackathon_notification.infrastructure.config;

import br.com.fiap.hackathon_notification.infrastructure.config.properties.RabbitQueuesProperties;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RabbitConfigurationTest {

	private final RabbitConfiguration configuration = new RabbitConfiguration();

	@Test
	void shouldCreateRabbitBeansWithDeadLetterConfiguration() {
		RabbitQueuesProperties properties = new RabbitQueuesProperties("video.completed", "video.failed");

		DirectExchange dlx = configuration.deadLetterExchange("app.notification.dlx");
		Queue completedQueue = configuration.videoCompletedQueue(properties, "app.notification.dlx");
		Queue failedQueue = configuration.videoFailedQueue(properties, "app.notification.dlx");
		Queue completedDlq = configuration.videoCompletedDeadLetterQueue(properties);
		Queue failedDlq = configuration.videoFailedDeadLetterQueue(properties);

		Binding completedBinding = configuration.videoCompletedDeadLetterBinding(completedDlq, dlx, properties);
		Binding failedBinding = configuration.videoFailedDeadLetterBinding(failedDlq, dlx, properties);

		assertEquals("app.notification.dlx", dlx.getName());

		assertEquals("video.completed", completedQueue.getName());
		assertEquals("app.notification.dlx", completedQueue.getArguments().get("x-dead-letter-exchange"));
		assertEquals("video.completed.dlq", completedQueue.getArguments().get("x-dead-letter-routing-key"));

		assertEquals("video.failed", failedQueue.getName());
		assertEquals("app.notification.dlx", failedQueue.getArguments().get("x-dead-letter-exchange"));
		assertEquals("video.failed.dlq", failedQueue.getArguments().get("x-dead-letter-routing-key"));

		assertEquals("video.completed.dlq", completedDlq.getName());
		assertEquals("video.failed.dlq", failedDlq.getName());

		assertEquals("video.completed.dlq", completedBinding.getDestination());
		assertEquals("video.completed.dlq", completedBinding.getRoutingKey());
		assertEquals("video.failed.dlq", failedBinding.getDestination());
		assertEquals("video.failed.dlq", failedBinding.getRoutingKey());

		assertNotNull(configuration.jackson2JsonMessageConverter());
	}
}
