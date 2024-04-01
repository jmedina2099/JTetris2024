package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.figure.Figure;
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
public class EnginePublisher implements Publisher<Figure>, Subscription {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Subscriber<? super Figure> subscriber;

	@Override
	public void subscribe(Subscriber<? super Figure> subscriber) {
		this.logger.debug("===> EnginePublisher.subscribe()");
		this.subscriber = subscriber;
		this.subscriber.onSubscribe(this);
	}

	public void sendMovementFigure(Figure figure) {
		this.logger.debug("===> EnginePublisher.sendMovementFigure()");
		this.subscriber.onNext(figure);

	}

	@Override
	public void request(long n) {
		this.logger.debug("===> EnginePublisher.request = " + n);
	}

	@Override
	public void cancel() {
		this.logger.debug("===> EnginePublisher.cancel()");
	}

}