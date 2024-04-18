package org.jmedina.jtetris.api.publisher;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.api.service.ConversationService;
import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.BoxDTO;
import org.jmedina.jtetris.common.publisher.CustomPublisher;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
@Service
public class BoardPublisher extends CustomPublisher<BoardOperation<BoxDTO>> {

	@Autowired
	private ConversationService conversationService;

	public BoardPublisher() {
		super(LogManager.getLogger(BoardPublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super BoardOperation<BoxDTO>> subscriber) {
		this.logger.debug("===> BoardPublisher.subscribe()");
		super.subscribe(subscriber);
		getBoardConversation();
	}

	private void getBoardConversation() {
		this.logger.debug("===> FigurePublisher.getBoardConversation()");
		try {
			super.disposable = this.conversationService.getBoardConversation().timeout(Duration.ofHours(1))
					.doOnNext(figure -> {
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
						return Flux.<BoardOperation<BoxDTO>>empty();
					}).subscribe(board -> {
						this.logger.debug("===> getBoardConversation.subscribe = " + board);
						super.addToQueue(board);
					});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}
}
