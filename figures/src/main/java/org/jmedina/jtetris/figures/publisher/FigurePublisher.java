package org.jmedina.jtetris.figures.publisher;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.common.publisher.CustomPublisher;
import org.jmedina.jtetris.figures.figure.BoxDB;
import org.jmedina.jtetris.figures.figure.FigureDB;
import org.jmedina.jtetris.figures.model.NextFigureOperation;
import org.jmedina.jtetris.figures.service.FigureService;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
public class FigurePublisher extends CustomPublisher<FigureOperation<BoxDB, FigureDB<BoxDB>>> {

	private FigureService figureService;
	private NextFigurePublisher nextFigurePublisher;

	public FigurePublisher(FigureService figureService, NextFigurePublisher nextFigurePublisher) {
		super(LogManager.getLogger(FigurePublisher.class));
		this.figureService = figureService;
		this.nextFigurePublisher = nextFigurePublisher;
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation<BoxDB, FigureDB<BoxDB>>> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		askAndSendForNextFigureOperation(); // first figure.
		getNextFigureConversation();
	}

	@Override
	public boolean stop() {
		this.nextFigurePublisher.stop();
		return super.stop();
	}

	private void getNextFigureConversation() {
		this.logger.debug("===> FigurePublisher.getNextFigureConversation()");
		try {
			Flux.from(this.nextFigurePublisher).timeout(Duration.ofHours(1)).doOnNext(op -> {
				this.logger.debug("===> ENGINE - Flux.from.nextFigurePublisher - NEXT = " + op);
			}).doOnComplete(() -> {
				this.logger.debug("===> ENGINE - Flux.from.nextFigurePublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> ENGINE - Flux.from.nextFigurePublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> ENGINE - Flux.from.nextFigurePublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - Flux.from.nextFigurePublisher =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - Flux.from.nextFigurePublisher =", e);
				return Flux.<NextFigureOperation>empty();
			}).subscribe(op -> {
				this.logger.debug("===> getNextFigureConversation.subscribe = " + op);
				if (FigureOperationEnumeration.NEW_OPERATION.equals(op.getOperation())) {
					this.logger.debug("===> NEW_OPERATION!!!");
					askAndSendForNextFigureOperation();
				}
			});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

	private void askAndSendForNextFigureOperation() {
		this.figureService.askForNextFigureOperation().subscribe(figure -> {
			super.addToQueue(figure);
		});
	}

}
