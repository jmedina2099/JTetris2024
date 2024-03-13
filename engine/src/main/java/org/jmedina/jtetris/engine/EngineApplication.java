package org.jmedina.jtetris.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactivefeign.spring.config.EnableReactiveFeignClients;

/**
 * @author Jorge Medina
 *
 */
@EnableReactiveFeignClients
@SpringBootApplication
public class EngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(EngineApplication.class, args);
	}
}