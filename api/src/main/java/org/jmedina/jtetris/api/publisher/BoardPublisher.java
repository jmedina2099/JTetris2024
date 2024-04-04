package org.jmedina.jtetris.api.publisher;

import org.jmedina.jtetris.api.client.EngineClient;
import org.jmedina.jtetris.api.model.BoardOperation;
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
public class BoardPublisher extends CustomPublisher<BoardOperation> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EngineClient engineClient;

	@Override
	public void subscribe(Subscriber<? super BoardOperation> subscriber) {
		this.logger.debug("===> BoardPublisher.subscribe()");
		super.subscribe(subscriber);
		getBoardConversation();
	}

	private void getBoardConversation() {
		this.logger.debug("===> BoardPublisher.getBoardConversation()");
		this.engineClient.getBoardConversation().doOnNext(figure -> {
			this.logger.debug("===> API - getBoardConversation - NEXT = " + figure);
		}).doOnComplete(() -> {
			this.logger.debug("===> API - getBoardConversation - COMPLETE!");
		}).doOnCancel(() -> {
			this.logger.debug("===> API - getBoardConversation - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> API - getBoardConversation - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR - getBoardConversation =", e);
		}).subscribe(op -> {
			this.logger.debug("===> api.getBoardConversation.subscribe = " + op);
			super.addToQueue(op);
		});
	}

}
