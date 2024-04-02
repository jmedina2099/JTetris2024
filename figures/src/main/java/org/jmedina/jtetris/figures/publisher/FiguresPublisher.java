package org.jmedina.jtetris.figures.publisher;

import org.jmedina.jtetris.figures.model.FigureOperation;
import org.jmedina.jtetris.figures.service.FigureService;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class FiguresPublisher implements Publisher<FigureOperation>, Subscription {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;
	private Subscriber<? super FigureOperation> subscriber;

	@Value("${use.weblux}")
	private boolean useWebflux;

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FiguresPublisher.subscribe()");
		this.subscriber = subscriber;
		if (this.useWebflux) {
			this.subscriber.onSubscribe(this);
		}
		askAndSendNextFigureOperation();
	}

	public void askAndSendNextFigureOperation() {
		this.logger.debug("===> FiguresPublisher.askAndSendNextFigure()");
		FigureOperation figureOperation = this.figureService.askForNextFigureOperation();
		if (this.useWebflux) {
			this.subscriber.onNext(figureOperation);
		}
	}

	@Override
	public void request(long n) {
		this.logger.debug("===> FiguresPublisher.request = " + n);
	}

	@Override
	public void cancel() {
		this.logger.debug("===> FiguresPublisher.cancel()");
	}

}
