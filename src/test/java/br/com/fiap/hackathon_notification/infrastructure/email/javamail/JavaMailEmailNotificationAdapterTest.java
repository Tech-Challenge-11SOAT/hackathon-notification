package br.com.fiap.hackathon_notification.infrastructure.email.javamail;

import br.com.fiap.hackathon_notification.domain.model.EmailNotificationResult;
import br.com.fiap.hackathon_notification.domain.model.VideoEventType;
import br.com.fiap.hackathon_notification.domain.model.VideoProcessingEvent;
import br.com.fiap.hackathon_notification.infrastructure.config.properties.EmailEngineProperties;
import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JavaMailEmailNotificationAdapterTest {

	@Mock
	private JavaMailSender mailSender;

	@Test
	void shouldSendMailSuccessfullyForCompletedEvent() throws Exception {
		EmailEngineProperties properties = new EmailEngineProperties("noreply@fiap.com", "fallback.com", null);
		JavaMailEmailNotificationAdapter adapter = new JavaMailEmailNotificationAdapter(mailSender, properties);

		MimeMessage mimeMessage = new MimeMessage((Session) null);
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

		VideoProcessingEvent event = new VideoProcessingEvent(
				UUID.randomUUID(),
				UUID.randomUUID(),
				"arquivo.mp4",
				"zip/key",
				Instant.now(),
				VideoEventType.COMPLETED);

		EmailNotificationResult result = adapter.send(event, "aluno@fiap.com");

		verify(mailSender).send(mimeMessage);
		assertTrue(result.success());
		assertEquals("aluno@fiap.com", result.recipientEmail());
		assertNull(result.errorMessage());
		assertNotNull(result.processedAt());

		assertEquals("Seu vídeo foi processado com sucesso", mimeMessage.getSubject());
		Address[] recipients = mimeMessage.getRecipients(MimeMessage.RecipientType.TO);
		assertEquals("aluno@fiap.com", ((InternetAddress) recipients[0]).getAddress());
	}

	@Test
	void shouldReturnFailureWhenMailSenderThrowsException() {
		EmailEngineProperties properties = new EmailEngineProperties("noreply@fiap.com", "fallback.com", null);
		JavaMailEmailNotificationAdapter adapter = new JavaMailEmailNotificationAdapter(mailSender, properties);

		MimeMessage mimeMessage = new MimeMessage((Session) null);
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
		doThrow(new RuntimeException("SMTP offline")).when(mailSender).send(mimeMessage);

		VideoProcessingEvent event = new VideoProcessingEvent(
				UUID.randomUUID(),
				UUID.randomUUID(),
				"arquivo.mp4",
				"zip/key",
				Instant.now(),
				VideoEventType.FAILED);

		EmailNotificationResult result = adapter.send(event, "aluno@fiap.com");

		assertFalse(result.success());
		assertEquals("aluno@fiap.com", result.recipientEmail());
		assertTrue(result.errorMessage().contains("SMTP offline"));
		assertNotNull(result.processedAt());
	}
}
