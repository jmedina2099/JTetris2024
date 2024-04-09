package org.jmedina.jtetris.api.publisher;

import org.jmedina.jtetris.api.model.BoardOperation;
import org.jmedina.jtetris.api.service.ConversationService;
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
public class BoardPublisher extends CustomPublisher<BoardOperation> {

	@Autowired
	private ConversationService conversationService;

	public BoardPublisher() {
		super(LoggerFactory.getLogger(BoardPublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super BoardOperation> subscriber) {
		this.logger.debug("===> BoardPublisher.subscribe()");
		super.subscribe(subscriber);
		getBoardConversation();
	}

	private void getBoardConversation() {
		this.logger.debug("===> FigurePublisher.getBoardConversation()");
		try {
			this.conversationService.getBoardConversation().doOnNext(figure -> {
				this.logger.debug("===> API - getBoardConversation - NEXT = " + figure);
			}).doOnComplete(() -> {
				this.logger.debug("===> API - getBoardConversation - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> API - getBoardConversation - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> API - getBoardConversation - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - getBoardConversation =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - getBoardConversation =", e);
				return Mono.empty();
			}).subscribe(board -> {
				this.logger.debug("===> getBoardConversation.subscribe = " + board);
				super.addToQueue(board);
			});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}
}
