package br.com.fiap.hackathon_notification.infrastructure.messaging.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record VideoEventMessage(
		@JsonProperty("VideoID") UUID videoId,
		@JsonProperty("UserID") UUID userId,
		@JsonProperty("OriginalFilename") String originalFilename,
		@JsonProperty("S3ZipKey") String s3ZipKey,
		@JsonProperty("CompletedAt") Instant completedAt) {
}
