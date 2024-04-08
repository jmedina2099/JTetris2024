package org.jmedina.jtetris.figures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.netty.ReactorNetty;

/**
 * @author Jorge Medina
 *
 */
@SpringBootApplication(exclude = JerseyServerMetricsAutoConfiguration.class)
public class FiguresApplication {

	public static void main(String[] args) {
		System.setProperty(ReactorNetty.POOL_MAX_IDLE_TIME, "3600000");
		System.setProperty(ReactorNetty.POOL_MAX_LIFE_TIME, "3600000");
		System.setProperty(ReactorNetty.POOL_LEASING_STRATEGY, "fifo");
		System.setProperty(ReactorNetty.POOL_MAX_CONNECTIONS, "200");
		System.setProperty(ReactorNetty.POOL_ACQUIRE_TIMEOUT, "3600000");
		System.setProperty("io.netty.tryReflectionSetAccessible", "true");
		SpringApplication.run(FiguresApplication.class, args);
	}
}