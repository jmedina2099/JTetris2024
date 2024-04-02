package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.model.BoardOperation;
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
public class BoardPublisher implements Publisher<BoardOperation>, Subscription {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Subscriber<? super BoardOperation> subscriber;

	@Value("${use.weblux}")
	private boolean useWebflux;

	@Override
	public void subscribe(Subscriber<? super BoardOperation> subscriber) {
		this.logger.debug("===> BoardPublisher.subscribe()");
		this.subscriber = subscriber;
		if (this.useWebflux) {
			this.subscriber.onSubscribe(this);
		}
	}

	public void sendBoard(BoardOperation board) {
		this.logger.debug("===> BoardPublisher.sendMovementFigure()");
		if (this.useWebflux) {
			this.subscriber.onNext(board);
		}
	}

	@Override
	public void request(long n) {
		this.logger.debug("===> BoardPublisher.request = " + n);
	}

	@Override
	public void cancel() {
		this.logger.debug("===> BoardPublisher.cancel()");
	}

}
