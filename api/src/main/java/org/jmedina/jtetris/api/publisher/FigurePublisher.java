package org.jmedina.jtetris.api.publisher;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.api.service.ConversationService;
import org.jmedina.jtetris.common.model.FigureOperation;
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
public class FigurePublisher extends CustomPublisher<FigureOperation> {

	@Autowired
	private ConversationService conversationService;

	public FigurePublisher() {
		super(LogManager.getLogger(FigurePublisher.class));
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
			super.disposable = this.conversationService.getFigureConversation().timeout(Duration.ofHours(1))
					.doOnNext(figure -> {
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
						return Flux.<FigureOperation>empty();
					}).subscribe(figure -> {
						this.logger.debug("===> getFigureConversation.subscribe = " + figure);
						super.addToQueue(figure);
					});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

}
