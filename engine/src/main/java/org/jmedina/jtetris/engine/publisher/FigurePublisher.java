package org.jmedina.jtetris.engine.publisher;

import java.time.Duration;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.service.EngineService;
import org.jmedina.jtetris.engine.service.FigureService;
import org.reactivestreams.Subscriber;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Service
public class FigurePublisher extends CustomPublisher<FigureOperation> {

	@Autowired
	private EngineService engineService;

	@Autowired
	private FigureService figureService;

	public FigurePublisher() {
		super();
		super.logger = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		getNextFigure();
	}

	public void getNextFigure() {
		this.logger.debug("===> FigurePublisher.getNextFigure()");
		this.figureService.getNextFigure().timeout(Duration.ofSeconds(5)).doOnNext(figure -> {
			this.logger.debug("===> ENGINE - getNextFigure - NEXT = " + figure);
		}).doOnCancel(() -> {
			this.logger.debug("===> ENGINE - getNextFigure - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> ENGINE - getNextFigure - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR - getNextFigure =", e);
		}).onErrorResume(e -> {
			this.logger.error("==*=> ERROR - getNextFigure =", e);
			return Mono.empty();
		}).subscribe(figure -> {
			this.logger.debug("===> engine.getNextFigure.subscribe = " + figure);
			this.engineService.addFigureOperation(figure);
			super.addToQueue(figure);
		});
	}
}
