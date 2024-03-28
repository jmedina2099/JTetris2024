package org.jmedina.jtetris.engine.service.impl;

import org.jmedina.jtetris.engine.client.FiguresClient;
import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.service.FigureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class FigureServiceImpl implements FigureService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FiguresClient figuresClient;

	@Override
	public Mono<Figure> askForNextFigure() {
		this.logger.debug("==> FigureService.askForNextFigure()");
		return this.figuresClient.askForNextFigure();
	}
}