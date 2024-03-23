package org.jmedina.jtetris.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactivefeign.spring.config.EnableReactiveFeignClients;

/**
 * @author Jorge Medina
 *
 */
@EnableReactiveFeignClients
@SpringBootApplication(exclude = JerseyServerMetricsAutoConfiguration.class)
public class TetrisApplication {

	public static void main(String[] args) {
		SpringApplication.run(TetrisApplication.class, args);
	}
}