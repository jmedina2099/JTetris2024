package org.jmedina.jtetris.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactivefeign.spring.config.EnableReactiveFeignClients;
import reactor.netty.ReactorNetty;

/**
 * @author Jorge Medina
 *
 */
@EnableReactiveFeignClients
@SpringBootApplication(exclude = JerseyServerMetricsAutoConfiguration.class)
public class EngineApplication {

	public static void main(String[] args) {
		System.setProperty(ReactorNetty.POOL_MAX_IDLE_TIME, "3600000");
		System.setProperty(ReactorNetty.POOL_MAX_LIFE_TIME, "3600000");
		System.setProperty(ReactorNetty.POOL_LEASING_STRATEGY, "fifo");
		System.setProperty(ReactorNetty.POOL_MAX_CONNECTIONS, "200");
		System.setProperty(ReactorNetty.POOL_ACQUIRE_TIMEOUT, "-1");
		System.setProperty("io.netty.tryReflectionSetAccessible", "true");
		SpringApplication.run(EngineApplication.class, args);
	}
}