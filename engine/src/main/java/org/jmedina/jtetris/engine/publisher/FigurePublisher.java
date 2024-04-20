package org.jmedina.jtetris.engine.publisher;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.common.publisher.CustomPublisher;
import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.jmedina.jtetris.engine.figure.FigureMotion;
import org.jmedina.jtetris.engine.service.ConversationService;
import org.jmedina.jtetris.engine.service.EngineService;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
public class FigurePublisher extends CustomPublisher<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>> {

	private EngineService engineService;
	private ConversationService conversationService;
	private String auth;

	public FigurePublisher(String auth, EngineService engineService, ConversationService conversationService) {
		super(LogManager.getLogger(FigurePublisher.class));
		this.auth = auth;
		this.engineService = engineService;
		this.conversationService = conversationService;
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation<BoxMotion, FigureMotion<BoxMotion>>> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		getFigureConversation();
	}

	private void getFigureConversation() {
		this.logger.debug("===> FigurePublisher.getFigureConversation()");
		try {
			super.disposable = this.conversationService.getFigureConversation(this.auth).timeout(Duration.ofHours(1))
					.doOnNext(figure -> {
						this.logger.debug("===> ENGINE - getFigureConversation - NEXT = " + figure);
					}).doOnComplete(() -> {
						this.logger.debug("===> ENGINE - getFigureConversation - COMPLETE!");
					}).doOnCancel(() -> {
						this.logger.debug("===> ENGINE - getFigureConversation - CANCEL!");
					}).doOnTerminate(() -> {
						this.logger.debug("===> ENGINE - getFigureConversation - TERMINATE!");
					}).doOnError(e -> {
						this.logger.error("==*=> ERROR - getFigureConversation =", e);
					}).onErrorResume(e -> {
						this.logger.error("==*=> ERROR - getFigureConversation =", e);
						return Flux.<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>>empty();
					}).subscribe(figure -> {
						this.logger.debug("===> getFigureConversation.subscribe = " + figure);
						this.engineService.addFigureOperation(figure);
						super.addToQueue(figure);
					});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

}
