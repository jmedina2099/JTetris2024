package org.jmedina.jtetris.engine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class FigureService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final ReactiveCircuitBreakerFactory<?, ?> reactiveCircuitBreakerFactory;
	private ReactiveCircuitBreaker circuitNextFigure;
	private WebClient webClient;
	
	@Value("${engine.figures.base-url}")
	public String figuresBaseUrl;

	@Value("${engine.figures.askForNextFigureUri}")
	public String askForNextFigureUri;

	@PostConstruct
	public void postConstruct() {
		this.logger.debug("==> FigureService.postConstruct()");
		this.circuitNextFigure = this.reactiveCircuitBreakerFactory.create("circuitNextFigure");
		this.webClient = WebClient.builder().baseUrl(this.figuresBaseUrl).build();
	}

	public void askForNextFigure() {
		this.logger.debug("==> FigureService.askForNextFigure()");
		RequestHeadersSpec<?> spec = webClient.get().uri( this.askForNextFigureUri );
		Mono<Boolean> response = this.circuitNextFigure.run(spec.retrieve().bodyToMono(Boolean.class), throwable -> {
			this.logger.warn("Error making request to figure service");
			return Mono.empty();
		});
		response.subscribe(value -> this.logger.debug("---> askForNextFigure = {}", value));
	}

}