package org.jmedina.jtetris.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactivefeign.spring.config.EnableReactiveFeignClients;

/**
 * @author Jorge Medina
 *
 */
@EnableReactiveFeignClients
@SpringBootApplication
public class TetrisApplication {

	public static void main(String[] args) {
		SpringApplication.run(TetrisApplication.class, args);
	}
}