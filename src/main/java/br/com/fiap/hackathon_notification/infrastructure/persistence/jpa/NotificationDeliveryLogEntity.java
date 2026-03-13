package br.com.fiap.hackathon_notification.infrastructure.persistence.jpa;

import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_delivery_log")
public class NotificationDeliveryLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(nullable = false, updatable = false, length = 36)
	private UUID videoId;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(nullable = false, updatable = false, length = 36)
	private UUID userId;

	@Column(nullable = false, updatable = false)
	private String originalFilename;

	@Column
	private String s3ZipKey;

	@Column(nullable = false, updatable = false)
	private Instant completedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private VideoEventType eventType;

	@Column(nullable = false, updatable = false)
	private String recipientEmail;

	@Column(nullable = false, updatable = false)
	private boolean emailSent;

	@Column
	private String providerMessageId;

	@Column(length = 4000)
	private String errorMessage;

	@Column(nullable = false, updatable = false)
	private Instant processedAt;

	@Column(nullable = false)
	private int attemptCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getVideoId() {
		return videoId;
	}

	public void setVideoId(UUID videoId) {
		this.videoId = videoId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getS3ZipKey() {
		return s3ZipKey;
	}

	public void setS3ZipKey(String s3ZipKey) {
		this.s3ZipKey = s3ZipKey;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Instant completedAt) {
		this.completedAt = completedAt;
	}

	public VideoEventType getEventType() {
		return eventType;
	}

	public void setEventType(VideoEventType eventType) {
		this.eventType = eventType;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public boolean isEmailSent() {
		return emailSent;
	}

	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}

	public String getProviderMessageId() {
		return providerMessageId;
	}

	public void setProviderMessageId(String providerMessageId) {
		this.providerMessageId = providerMessageId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Instant getProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(Instant processedAt) {
		this.processedAt = processedAt;
	}

	public int getAttemptCount() {
		return attemptCount;
	}

	public void setAttemptCount(int attemptCount) {
		this.attemptCount = attemptCount;
	}
}
