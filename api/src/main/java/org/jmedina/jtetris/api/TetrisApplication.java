package org.jmedina.jtetris.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Jorge Medina
 *
 */
@SpringBootApplication
@EnableFeignClients
public class TetrisApplication {

	public static void main(String[] args) {
		SpringApplication.run(TetrisApplication.class, args);
	}

}