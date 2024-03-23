package org.jmedina.jtetris.figures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jorge Medina
 *
 */
@SpringBootApplication(exclude = JerseyServerMetricsAutoConfiguration.class)
public class FiguresApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiguresApplication.class, args);
	}
}