package org.jmedina.jtetris.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactivefeign.webclient.WebClientFeignCustomizer;

/**
 * @author Jorge Medina
 *
 */
@Configuration
public class FeignConfig {

	@Bean
	WebClientFeignCustomizer webClientFeignCustomizer() {
		return webClient -> webClient.defaultHeader("Connection", "keep-alive").defaultHeader("Keep-Alive",
				"timeout=3600");
	}
}
