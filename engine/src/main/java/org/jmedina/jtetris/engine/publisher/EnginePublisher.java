package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.service.FigureService;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class EnginePublisher implements Publisher<Figure> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;
	private Subscriber<? super Figure> subscriber;

	@Override
	public void subscribe(Subscriber<? super Figure> subscriber) {
		this.logger.debug("===> EnginePublisher.subscribe()");
		this.subscriber = subscriber;
		start();
	}

	private void start() {
		this.logger.debug("===> EnginePublisher.start()");
		this.figureService.start().subscribe(this.subscriber);
	}

	public void askForNextFigure() {
		this.logger.debug("===> EnginePublisher.askForNextFigure()");
		this.logger.debug("----> mono.subscribe..");
		this.figureService.askForNextFigure().subscribe();
	}

}
