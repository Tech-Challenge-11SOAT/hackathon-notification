package br.com.fiap.hackathon_notification.infrastructure.email.emailengine;

import java.util.List;

public record EmailEngineSubmitRequest(
		String from,
		List<String> to,
		String subject,
		String text) {
}
