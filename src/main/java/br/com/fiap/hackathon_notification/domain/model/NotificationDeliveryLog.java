package br.com.fiap.hackathon_notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public record NotificationDeliveryLog(
		UUID videoId,
		UUID userId,
		String originalFilename,
		String s3ZipKey,
		Instant completedAt,
		VideoEventType eventType,
		String recipientEmail,
		boolean emailSent,
		String providerMessageId,
		String errorMessage,
		Instant processedAt,
		int attemptCount) {
}
