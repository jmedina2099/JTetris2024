package org.jmedina.jtetris.engine.service.impl;

import org.jmedina.jtetris.engine.client.FiguresClient;
import org.jmedina.jtetris.engine.model.FigureOperation;
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
	public Mono<FigureOperation> getNextFigure() {
		this.logger.debug("==> FigureService.getNextFigure()");
		return this.figuresClient.getNextFigure();
	}

	@Override
	public Mono<Boolean> stop() {
		this.logger.debug("==> FigureService.stop()");
		return this.figuresClient.stop();
	}
}