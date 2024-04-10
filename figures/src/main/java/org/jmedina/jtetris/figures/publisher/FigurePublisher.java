package org.jmedina.jtetris.figures.publisher;

import java.time.Duration;

import org.jmedina.jtetris.figures.model.FigureOperation;
import org.jmedina.jtetris.figures.model.Message;
import org.jmedina.jtetris.figures.service.ConversationService;
import org.jmedina.jtetris.figures.service.FigureService;
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
public class FigurePublisher extends CustomPublisher<FigureOperation> {

	@Autowired
	private FigureService figureService;

	@Autowired
	private ConversationService conversationService;

	public FigurePublisher() {
		super(LoggerFactory.getLogger(FigurePublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		getNextFigureConversation();
	}

	private void getNextFigureConversation() {
		this.logger.debug("===> FigurePublisher.getNextFigureConversation()");
		try {
			this.conversationService.getNextFigureConversation().timeout(Duration.ofHours(1)).doOnNext(message -> {
				this.logger.debug("===> ENGINE - getNextFigureConversation - NEXT = " + message);
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
				return Flux.<Message>empty();
			}).subscribe(message -> {
				this.logger.debug("===> getNextFigureConversation.subscribe = " + message);
				super.addToQueue(this.figureService.askForNextFigureOperation());
			});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

}
