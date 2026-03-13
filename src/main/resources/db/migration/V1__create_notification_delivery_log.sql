CREATE TABLE notification_delivery_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    video_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    s3_zip_key VARCHAR(512) NULL,
    completed_at TIMESTAMP(6) NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    email_sent BOOLEAN NOT NULL,
    provider_message_id VARCHAR(255) NULL,
    error_message VARCHAR(4000) NULL,
    processed_at TIMESTAMP(6) NOT NULL,
    attempt_count INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_notification_delivery_log_event UNIQUE (video_id, event_type)
);
