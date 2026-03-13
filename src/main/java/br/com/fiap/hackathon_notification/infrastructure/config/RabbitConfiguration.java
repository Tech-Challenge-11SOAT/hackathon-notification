package br.com.fiap.hackathon_notification.infrastructure.config;

import br.com.fiap.hackathon_notification.infrastructure.config.properties.RabbitQueuesProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitConfiguration {

	@Bean
	DirectExchange deadLetterExchange(@Value("${app.rabbit.dlx}") String deadLetterExchangeName) {
		return new DirectExchange(deadLetterExchangeName, true, false);
	}

	@Bean
	Queue videoCompletedQueue(
			RabbitQueuesProperties rabbitQueuesProperties,
			@Value("${app.rabbit.dlx}") String deadLetterExchangeName) {
		return new Queue(rabbitQueuesProperties.videoCompleted(), true, false, false,
				Map.of(
						"x-dead-letter-exchange", deadLetterExchangeName,
						"x-dead-letter-routing-key", rabbitQueuesProperties.videoCompletedDlqRoutingKey()));
	}

	@Bean
	Queue videoFailedQueue(
			RabbitQueuesProperties rabbitQueuesProperties,
			@Value("${app.rabbit.dlx}") String deadLetterExchangeName) {
		return new Queue(rabbitQueuesProperties.videoFailed(), true, false, false,
				Map.of(
						"x-dead-letter-exchange", deadLetterExchangeName,
						"x-dead-letter-routing-key", rabbitQueuesProperties.videoFailedDlqRoutingKey()));
	}

	@Bean
	Queue videoCompletedDeadLetterQueue(RabbitQueuesProperties rabbitQueuesProperties) {
		return new Queue(rabbitQueuesProperties.videoCompletedDlq(), true);
	}

	@Bean
	Queue videoFailedDeadLetterQueue(RabbitQueuesProperties rabbitQueuesProperties) {
		return new Queue(rabbitQueuesProperties.videoFailedDlq(), true);
	}

	@Bean
	Binding videoCompletedDeadLetterBinding(
			@Qualifier("videoCompletedDeadLetterQueue") Queue videoCompletedDeadLetterQueue,
			DirectExchange deadLetterExchange,
			RabbitQueuesProperties rabbitQueuesProperties) {
		return BindingBuilder.bind(videoCompletedDeadLetterQueue)
				.to(deadLetterExchange)
				.with(rabbitQueuesProperties.videoCompletedDlqRoutingKey());
	}

	@Bean
	Binding videoFailedDeadLetterBinding(
			@Qualifier("videoFailedDeadLetterQueue") Queue videoFailedDeadLetterQueue,
			DirectExchange deadLetterExchange,
			RabbitQueuesProperties rabbitQueuesProperties) {
		return BindingBuilder.bind(videoFailedDeadLetterQueue)
				.to(deadLetterExchange)
				.with(rabbitQueuesProperties.videoFailedDlqRoutingKey());
	}

	@Bean
	JacksonJsonMessageConverter jackson2JsonMessageConverter() {
		return new JacksonJsonMessageConverter();
	}
}
