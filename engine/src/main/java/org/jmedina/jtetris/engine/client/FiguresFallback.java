package org.jmedina.jtetris.engine.client;

import org.jmedina.jtetris.engine.model.Message;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class FiguresFallback implements FiguresClient {

	@Override
	public Mono<Message> askForNextFigure() {
		return Mono.just(new Message("ASK FOR NEXT FIGURE FALLBACK"));
	}
}