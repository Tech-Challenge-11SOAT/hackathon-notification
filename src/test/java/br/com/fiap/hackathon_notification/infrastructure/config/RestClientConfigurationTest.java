package br.com.fiap.hackathon_notification.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestClientConfigurationTest {

	private final RestClientConfiguration configuration = new RestClientConfiguration();

	@Test
	void shouldCreateRestClientBuilder() {
		RestClient.Builder builder = configuration.restClientBuilder();

		assertNotNull(builder);
		assertNotNull(builder.build());
	}
}
