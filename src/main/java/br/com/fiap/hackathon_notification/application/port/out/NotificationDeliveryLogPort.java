package br.com.fiap.hackathon_notification.application.port.out;

import br.com.fiap.hackathon_notification.domain.model.NotificationDeliveryLog;

public interface NotificationDeliveryLogPort {

	void save(NotificationDeliveryLog deliveryLog);
}
