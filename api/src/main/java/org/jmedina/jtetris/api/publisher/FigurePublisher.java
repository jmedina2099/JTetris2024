package org.jmedina.jtetris.api.publisher;

import org.jmedina.jtetris.api.model.FigureOperation;
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
public class FigurePublisher extends CustomPublisher<FigureOperation> {

	@Autowired
	private ConversationService conversationService;

	public FigurePublisher() {
		super(LoggerFactory.getLogger(FigurePublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		getFigureConversation();
	}

	private void getFigureConversation() {
		this.logger.debug("===> FigurePublisher.getFigureConversation()");
		try {
			this.conversationService.getFigureConversation().doOnNext(figure -> {
				this.logger.debug("===> API - getFigureConversation - NEXT = " + figure);
			}).doOnComplete(() -> {
				this.logger.debug("===> API - getFigureConversation - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> API - getFigureConversation - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> API - getFigureConversation - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - getFigureConversation =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - getFigureConversation =", e);
				return Mono.empty();
			}).subscribe(figure -> {
				this.logger.debug("===> getFigureConversation.subscribe = " + figure);
				super.addToQueue(figure);
			});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

}
