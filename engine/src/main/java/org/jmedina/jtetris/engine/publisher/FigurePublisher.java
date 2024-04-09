package org.jmedina.jtetris.engine.publisher;

import java.time.Duration;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.service.ConversationService;
import org.jmedina.jtetris.engine.service.EngineService;
import org.jmedina.jtetris.engine.service.FigureService;
import org.reactivestreams.Subscriber;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Service
public class FigurePublisher extends CustomPublisher<FigureOperation> {

	@Autowired
	private EngineService engineService;

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
		getFigureConversation();
	}

	private void getFigureConversation() {
		this.logger.debug("===> FigurePublisher.getFigureConversation()");
		try {
			Flux.from(this.conversationService.getFigureConversation()).doOnNext(figure -> {
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
				return Mono.empty();
			}).subscribe(figure -> {
				this.logger.debug("===> getFigureConversation.subscribe = " + figure);
				this.engineService.addFigureOperation(figure);
				super.addToQueue(figure);
			});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

	public void askForNextFigureOperation() {
		this.logger.debug("===> FigurePublisher.askForNextFigureOperation()");
		try {
			this.figureService.askForNextFigureOperation().timeout(Duration.ofSeconds(5)).doOnNext(value -> {
				this.logger.debug("===> ENGINE - askForNextFigureOperation - NEXT = " + value);
			}).doOnCancel(() -> {
				this.logger.debug("===> ENGINE - askForNextFigureOperation - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> ENGINE - askForNextFigureOperation - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - askForNextFigureOperation =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - askForNextFigureOperation =", e);
				return Mono.empty();
			}).subscribe(value -> {
				this.logger.debug("===> askForNextFigureOperation.subscribe = " + value);
			});
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}
}
