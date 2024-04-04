package org.jmedina.jtetris.api.publisher;

import java.time.Duration;

import org.jmedina.jtetris.api.client.EngineClient;
import org.jmedina.jtetris.api.model.FigureOperation;
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
	private final EngineClient engineClient;

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		getFigureConversation();
	}

	private void getFigureConversation() {
		this.logger.debug("===> FigurePublisher.getFigureConversation()");
		this.engineClient.getFigureConversation().timeout(Duration.ofHours(1)).doOnNext(figure -> {
			this.logger.debug("===> API - getFigureConversation - NEXT = " + figure);
		}).doOnComplete(() -> {
			this.logger.debug("===> API - getFigureConversation - COMPLETE!");
		}).doOnCancel(() -> {
			this.logger.debug("===> API - getFigureConversation - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> API - getFigureConversation - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR - getFigureConversation =", e);
		}).subscribe(op -> {
			this.logger.debug("===> api.getFigureConversation.subscribe = " + op);
			super.addToQueue(op);
		});
	}

}
