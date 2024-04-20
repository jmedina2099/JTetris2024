package org.jmedina.jtetris.api.publisher;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.api.service.ConversationService;
import org.jmedina.jtetris.common.model.BoxDTO;
import org.jmedina.jtetris.common.model.FigureDTO;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.common.publisher.CustomPublisher;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
public class FigurePublisher extends CustomPublisher<FigureOperation<BoxDTO, FigureDTO<BoxDTO>>> {

	private ConversationService conversationService;
	private String auth;

	public FigurePublisher(String auth, ConversationService conversationService) {
		super(LogManager.getLogger(FigurePublisher.class));
		this.auth = auth;
		this.conversationService = conversationService;
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation<BoxDTO, FigureDTO<BoxDTO>>> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		getFigureConversation();
	}

	private void getFigureConversation() {
		this.logger.debug("===> FigurePublisher.getFigureConversation()");
		try {
			super.disposable = this.conversationService.getFigureConversation(this.auth).timeout(Duration.ofHours(1))
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
						return Flux.<FigureOperation<BoxDTO, FigureDTO<BoxDTO>>>empty();
					}).subscribe(figure -> {
						this.logger.debug("===> getFigureConversation.subscribe = " + figure);
						super.addToQueue(figure);
					});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

}
