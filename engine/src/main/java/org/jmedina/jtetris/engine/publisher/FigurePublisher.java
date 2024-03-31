package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.service.FigureService;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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
public class FigurePublisher implements Publisher<Figure>, Subscription {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;
	private Subscriber<? super Figure> subscriber;

	@Override
	public void subscribe(Subscriber<? super Figure> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");

		this.logger.debug("----> onSubscribe..");
		subscriber.onSubscribe(this);

		askForNextFigure();

		this.subscriber = subscriber;
	}

	private void askForNextFigure() {
		this.logger.debug("----> mono.subscribe..");
		Mono<Figure> mono = this.figureService.askForNextFigure();
		mono.subscribe(figure -> {
			this.logger.debug("----> on next..");
			subscriber.onNext(figure);
		});
	}

	public void askAndSendNextFigure() {
		askForNextFigure();
	}

	@Override
	public void request(long n) {
		this.logger.debug("===> FigurePublisher.request = " + n);
	}

	@Override
	public void cancel() {
		this.logger.debug("===> FigurePublisher.cancel()");
	}

}
