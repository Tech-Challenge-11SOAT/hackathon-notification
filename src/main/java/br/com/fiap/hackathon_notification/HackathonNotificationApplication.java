package br.com.fiap.hackathon_notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HackathonNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackathonNotificationApplication.class, args);
	}

}
