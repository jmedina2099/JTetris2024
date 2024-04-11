package org.jmedina.jtetris.figures.publisher;

import java.time.Duration;

import org.jmedina.jtetris.figures.model.NextFigureOperation;
import org.jmedina.jtetris.figures.service.ConversationService;
import org.reactivestreams.Subscriber;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
@Service
public class NextFigurePublisher extends CustomPublisher<NextFigureOperation> {

	@Autowired
	private ConversationService conversationService;

	public NextFigurePublisher() {
		super(LoggerFactory.getLogger(NextFigurePublisher.class));
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
			this.conversationService.getNextFigureConversation().timeout(Duration.ofHours(1)).doOnNext(op -> {
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
