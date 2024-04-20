package org.jmedina.jtetris.engine.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class FiguresFallback implements FiguresClient {

	public Mono<Boolean> isUp(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	public Mono<Boolean> start(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	public Mono<Boolean> stop(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}
}