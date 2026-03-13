package br.com.fiap.hackathon_notification.infrastructure.email.emailengine;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmailEngineSubmitResponse(
		@JsonProperty("messageId") String messageId) {
}
