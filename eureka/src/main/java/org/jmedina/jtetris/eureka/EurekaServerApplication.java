package org.jmedina.jtetris.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Jorge Medina
 *
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		System.setProperty("log4j2.disable.jmx", "true");
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}