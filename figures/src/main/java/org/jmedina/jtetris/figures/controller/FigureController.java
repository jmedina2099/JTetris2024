package org.jmedina.jtetris.figures.controller;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.model.FigureOperation;
import org.jmedina.jtetris.figures.model.Message;
import org.jmedina.jtetris.figures.publisher.FiguresPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping
public class FigureController {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final FiguresPublisher figurePublisher;

	@GetMapping("/hello")
	public Mono<Message> hello() {
		this.logger.debug("===> FigureController.hello()");
		try {
			return Mono.just(new Message("Hello from figures reactive!!!"));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.empty();
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<FigureOperation> getFigureConversation() {
		this.logger.debug("===> FigureController.getFigureConversation()");
		Flux<FigureOperation> fluxOfFigures = null;
		try {
			fluxOfFigures = Flux.from(this.figurePublisher).doOnNext(figure -> {
				this.logger.debug("===> FIGURES - Flux.from.figurePublisher - NEXT = " + figure);
			}).doOnComplete(() -> {
				this.logger.debug("===> FIGURES - Flux.from.figurePublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> FIGURES - Flux.from.figurePublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> FIGURES - Flux.from.figurePublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR =", e);
			});
			return fluxOfFigures.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.empty();
		}
	}

	@PostMapping(value = "/askForNextFigure", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> askAndSendNextFigure() {
		this.logger.debug("===> FigureController.askAndSendNextFigure()");
		try {
			this.figurePublisher.askAndSendNextFigureOperation();
			return Mono.just(true).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false);
		}
	}

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop() {
		try {
			return Mono.just(this.figurePublisher.stop());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false);
		}
	}
}