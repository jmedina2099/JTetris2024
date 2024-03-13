package org.jmedina.jtetris.engine.service;

import org.jmedina.jtetris.engine.client.FiguresClient;
import org.jmedina.jtetris.engine.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Autowired
	private FiguresClient figuresClient;

	public Mono<Message> askForNextFigure() {
		this.logger.debug("==> FigureService.askForNextFigure()");
		return this.figuresClient.askForNextFigure();
	}
}