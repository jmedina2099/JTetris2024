package org.jmedina.jtetris.engine.controller;

import java.time.Duration;
import java.util.Optional;

import org.jmedina.jtetris.engine.model.BoardOperation;
import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.model.Message;
import org.jmedina.jtetris.engine.publisher.BoardPublisher;
import org.jmedina.jtetris.engine.publisher.FigurePublisher;
import org.jmedina.jtetris.engine.service.EngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = { "http://localhost:9081", "http://localhost:9083" })
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EngineController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EngineService engineService;
	private final FigurePublisher figurePublisher;
	private final BoardPublisher boardPublisher;

	@GetMapping("/hello")
	public Mono<Message> hello() {
		this.logger.debug("===> EngineController.hello()");
		try {
			return Mono.just(new Message("Hello from engine reactive!!!"));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.empty();
		}
	}

	@PostMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> start() {
		this.logger.debug("===> EngineController.start()");
		try {
			this.engineService.start(this.figurePublisher);
			return Mono.just(true).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop() {
		this.logger.debug("===> EngineController.stop()");
		try {
			this.engineService.stop();
			this.figurePublisher.stop();
			this.boardPublisher.stop();
			return Mono.just(true).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<FigureOperation> getFigureConversation() {
		this.logger.debug("===> EngineController.getFigureConversation()");
		Flux<FigureOperation> fluxOfFigures = null;
		try {
			fluxOfFigures = Flux.from(this.figurePublisher).doOnNext(figure -> {
						this.logger.debug("===> ENGINE - Flux.from.figurePublisher - NEXT = " + figure);
					}).doOnComplete(() -> {
						this.logger.debug("===> ENGINE - Flux.from.figurePublisher - COMPLETE!");
					}).doOnCancel(() -> {
						this.logger.debug("===> ENGINE - Flux.from.figurePublisher - CANCEL!");
					}).doOnTerminate(() -> {
						this.logger.debug("===> ENGINE - Flux.from.figurePublisher - TERMINATE!");
					}).doOnError(e -> {
						this.logger.error("==*=> ERROR =", e);
					});
			return fluxOfFigures.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.empty();
		}
	}

	@GetMapping(value = "/getBoardConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<BoardOperation> getBoardConversation() {
		this.logger.debug("===> EngineController.getBoardConversation()");
		Flux<BoardOperation> fluxOfBoards = null;
		try {
			fluxOfBoards = Flux.from(this.boardPublisher).doOnNext(figure -> {
				this.logger.debug("===> ENGINE - Flux.from.boardPublisher - NEXT = " + figure);
			}).doOnComplete(() -> {
				this.logger.debug("===> ENGINE - Flux.from.boardPublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> ENGINE - Flux.from.boardPublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> ENGINE - Flux.from.boardPublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR =", e);
			});
			return fluxOfBoards.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.empty();
		}
	}

	@PostMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveRight() {
		this.logger.debug("===> EngineController.moveRight()");
		try {
			return convertOptionalToMonoBoolean(this.engineService.moveRight());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveLeft() {
		this.logger.debug("===> EngineController.moveLeft()");
		try {
			return convertOptionalToMonoBoolean(this.engineService.moveLeft());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateRight() {
		this.logger.debug("===> EngineController.rotateRight()");
		try {
			return convertOptionalToMonoBoolean(this.engineService.rotateRight());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateLeft() {
		this.logger.debug("===> EngineController.rotateLeft()");
		try {
			return convertOptionalToMonoBoolean(this.engineService.rotateLeft());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> bottomDown() {
		this.logger.debug("===> EngineController.bottomDown()");
		try {
			return convertOptionalToMonoBoolean(this.engineService.bottomDown());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	private Mono<Boolean> convertOptionalToMonoBoolean(Optional<Boolean> optional) {
		return optional.isPresent() ? Mono.just(optional.get()).timeout(Duration.ofSeconds(3)) : Mono.just(false).timeout(Duration.ofSeconds(3));
	}

}