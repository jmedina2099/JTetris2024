package org.jmedina.jtetris.figures.publisher;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.common.publisher.CustomPublisher;
import org.jmedina.jtetris.figures.model.NextFigureOperation;
import org.jmedina.jtetris.figures.service.ConversationService;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
public class NextFigurePublisher extends CustomPublisher<NextFigureOperation> {

	private ConversationService conversationService;
	private String auth;

	public NextFigurePublisher(String auth, ConversationService conversationService) {
		super(LogManager.getLogger(NextFigurePublisher.class));
		this.auth = auth;
		this.conversationService = conversationService;
	}

	@Override
	public void subscribe(Subscriber<? super NextFigureOperation> subscriber) {
		this.logger.debug("===> NextFigurePublisher.subscribe()");
		super.subscribe(subscriber);
		getNextFigureConversation();
	}

	private void getNextFigureConversation() {
		this.logger.debug("===> FigurePublisher.getNextFigureConversation()");
		try {
			super.disposable = this.conversationService.getNextFigureConversation(this.auth)
					.timeout(Duration.ofHours(1)).doOnNext(op -> {
						this.logger.debug("===> ENGINE - getNextFigureConversation - NEXT = " + op);
					}).doOnComplete(() -> {
						this.logger.debug("===> ENGINE - getNextFigureConversation - COMPLETE!");
					}).doOnCancel(() -> {
						this.logger.debug("===> ENGINE - getNextFigureConversation - CANCEL!");
					}).doOnTerminate(() -> {
						this.logger.debug("===> ENGINE - getNextFigureConversation - TERMINATE!");
					}).doOnError(e -> {
						this.logger.error("==*=> ERROR - getNextFigureConversation =", e);
					}).onErrorResume(e -> {
						this.logger.error("==*=> ERROR - getNextFigureConversation =", e);
						return Flux.<NextFigureOperation>empty();
					}).subscribe(op -> {
						this.logger.debug("===> getNextFigureConversation.subscribe = " + op);
						super.addToQueue(op);
					});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

}
