package org.jmedina.jtetris.figures.publisher;

import org.jmedina.jtetris.figures.figure.Figure;
import org.jmedina.jtetris.figures.service.FigureService;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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
public class FiguresPublisher implements Publisher<Figure>, Subscription {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;
	private Subscriber<? super Figure> subscriber;

	@Override
	public void subscribe(Subscriber<? super Figure> subscriber) {
		this.logger.debug("===> FiguresPublisher.subscribe()");
		this.subscriber = subscriber;
		this.subscriber.onSubscribe(this);
		askAndSendNextFigure();
	}

	public void askAndSendNextFigure() {
		this.logger.debug("===> FiguresPublisher.askAndSendNextFigure()");
		this.subscriber.onNext(this.figureService.askForNextFigure());
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
