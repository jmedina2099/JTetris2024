package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.service.EngineService;
import org.jmedina.jtetris.engine.service.FigureService;
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
public class FigurePublisher extends CustomPublisher<FigureOperation> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EngineService engineService;
	private final FigureService figureService;

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		getFigureConversation();
	}

	public boolean stop() {
		super.stop();
		this.figureService.stop().subscribe();
		return true;
	}

	public void sendFigureOperation(FigureOperation figureOperation) {
		this.logger.debug("===> EnginePublisher.sendFigureOperation() = " + figureOperation);
		super.addToQueue(figureOperation);
	}

	public void askForNextFigure() {
		this.logger.debug("===> FigurePublisher.askForNextFigure()");
		this.figureService.askForNextFigure().doOnNext(figure -> {
			this.logger.debug("===> ENGINE - askForNextFigure - NEXT = " + figure);
		}).doOnCancel(() -> {
			this.logger.debug("===> ENGINE - askForNextFigure - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> ENGINE - askForNextFigure - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR =", e);
		}).subscribe((Boolean value) -> {
			this.logger.debug("===> engine.askForNextFigure.subscribe = " + value);
		});
	}

	private void getFigureConversation() {
		this.logger.debug("===> FigurePublisher.getFigureConversation()");
		this.figureService.getFigureConversation().doOnNext(figure -> {
			this.logger.debug("===> ENGINE - getFigureConversation - NEXT = " + figure);
		}).doOnComplete(() -> {
			this.logger.debug("===> ENGINE - getFigureConversation - COMPLETE!");
		}).doOnCancel(() -> {
			this.logger.debug("===> ENGINE - getFigureConversation - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> ENGINE - getFigureConversation - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR =", e);
		}).subscribe(op -> {
			this.logger.debug("===> engine.getFigureConversation.subscribe = " + op);
			this.engineService.addFigureOperation(op);
			super.addToQueue(op);
		});
	}

}
