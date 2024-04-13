package org.jmedina.jtetris.engine.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.engine.client.FiguresClient;
import org.jmedina.jtetris.engine.service.FigureService;
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

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final FiguresClient figuresClient;

	@Override
	public Mono<Boolean> stop() {
		this.logger.debug("==> FigureService.stop()");
		return this.figuresClient.stop();
	}
}