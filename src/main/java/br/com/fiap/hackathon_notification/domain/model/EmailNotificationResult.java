package br.com.fiap.hackathon_notification.domain.model;

import java.time.Instant;

public record EmailNotificationResult(
		boolean success,
		String recipientEmail,
		String providerMessageId,
		String errorMessage,
		Instant processedAt) {
}
