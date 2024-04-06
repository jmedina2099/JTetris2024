package org.jmedina.jtetris.api.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.Options;

import io.undertow.UndertowOptions;

/**
 * @author Jorge Medina
 *
 */
@Configuration
public class UndertowConfig {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean
	UndertowServletWebServerFactory undertowServletWebServerFactory() {
		Duration duration = Duration.ofHours(1);
		final int timeout = (int) duration.toMillis();
		this.logger.debug("===> API - undertow - timeout = " + duration.toMinutes());
		final UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
		factory.addBuilderCustomizers(builder -> {
			builder.setServerOption(UndertowOptions.NO_REQUEST_TIMEOUT, timeout);
			builder.setServerOption(UndertowOptions.IDLE_TIMEOUT, timeout);
			builder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, true);
			builder.setSocketOption(Options.READ_TIMEOUT, timeout);
			builder.setSocketOption(Options.WRITE_TIMEOUT, timeout);
			builder.setSocketOption(Options.KEEP_ALIVE, true);
		});
		return factory;
	}
}
