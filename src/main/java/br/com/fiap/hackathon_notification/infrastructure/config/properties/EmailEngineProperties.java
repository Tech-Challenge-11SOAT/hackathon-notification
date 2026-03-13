package br.com.fiap.hackathon_notification.infrastructure.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.email-engine")
public record EmailEngineProperties(
		@NotBlank String from,
		@NotBlank String toFallbackDomain,
		String defaultRecipient) {
}
