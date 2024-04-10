package org.jmedina.jtetris.engine.controller;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jmedina.jtetris.engine.client.FiguresClient;
import org.jmedina.jtetris.engine.model.BoardOperation;
import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.model.Message;
import org.jmedina.jtetris.engine.publisher.BoardPublisher;
import org.jmedina.jtetris.engine.publisher.EnginePublisher;
import org.jmedina.jtetris.engine.publisher.FigurePublisher;
import org.jmedina.jtetris.engine.publisher.NextFigurePublisher;
import org.jmedina.jtetris.engine.service.EngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:9081", "http://localhost:9082", "http://localhost:9083" })
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EngineController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EngineService engineService;
	private final EnginePublisher enginePublisher;
	private final FigurePublisher figurePublisher;
	private final BoardPublisher boardPublisher;
	private final NextFigurePublisher nextFigurePublisher;
	private final FiguresClient figuresClient;

	@GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
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
			this.engineService.start(this.nextFigurePublisher, this.enginePublisher);
			this.nextFigurePublisher.sendNextFigurePetition(new Message("OK"));
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
			this.figurePublisher.stop();
			this.enginePublisher.stop();
			this.boardPublisher.stop();
			this.nextFigurePublisher.stop();
			this.engineService.stop();
			return this.figuresClient.stop().timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<FigureOperation> getFigureConversation(ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.getFigureConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		this.logger.debug("=========> HEADERS!!!");
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> keys = headers.keySet();
		keys.stream().forEach(key -> {
			List<String> values = headers.get(key);
			if (values != null) {
				values.stream().forEach(v -> {
					this.logger.debug("=========> header= {}: {}", key, v);
				});
			}
		});
		this.logger.debug("=========> END HEADERS!!!");
		Flux<FigureOperation> fluxFromFigures = null;
		Flux<FigureOperation> fluxFromEngine = null;
		try {
			fluxFromFigures = Flux.from(this.figurePublisher).doOnNext(figure -> {
				this.logger.debug("===> ENGINE - Flux.from.figurePublisher - NEXT = " + figure);
			}).doOnComplete(() -> {
				this.logger.debug("===> ENGINE - Flux.from.figurePublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> ENGINE - Flux.from.figurePublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> ENGINE - Flux.from.figurePublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - Flux.from.figurePublisher =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - Flux.from.figurePublisher =", e);
				return Flux.<FigureOperation>empty();
			});
			fluxFromEngine = Flux.from(this.enginePublisher).doOnNext(figure -> {
				this.logger.debug("===> ENGINE - Flux.from.enginePublisher - NEXT = " + figure);
			}).doOnComplete(() -> {
				this.logger.debug("===> ENGINE - Flux.from.enginePublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> ENGINE - Flux.from.enginePublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> ENGINE - Flux.from.enginePublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - Flux.from.enginePublisher =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - Flux.from.enginePublisher =", e);
				return Flux.<FigureOperation>empty();
			});
			return fluxFromFigures.mergeWith(fluxFromEngine).timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.<FigureOperation>empty();
		}
	}

	@GetMapping(value = "/getBoardConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<BoardOperation> getBoardConversation(ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.getBoardConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
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
				this.logger.error("==*=> ERROR - Flux.from.boardPublisher =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - Flux.from.boardPublisher =", e);
				return Flux.<BoardOperation>empty();
			});
			return fluxOfBoards.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.<BoardOperation>empty();
		}
	}

	@GetMapping(value = "/getNextFigureConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Message> getNextFigureConversation(ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.getNextFigureConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		this.logger.debug("=========> HEADERS!!!");
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> keys = headers.keySet();
		keys.stream().forEach(key -> {
			List<String> values = headers.get(key);
			if (values != null) {
				values.stream().forEach(v -> {
					this.logger.debug("=========> header= {}: {}", key, v);
				});
			}
		});
		this.logger.debug("=========> END HEADERS!!!");
		Flux<Message> fluxOfNextFigure = null;
		try {
			fluxOfNextFigure = Flux.from(this.nextFigurePublisher).doOnNext(value -> {
				this.logger.debug("===> ENGINE - Flux.from.nextFigurePublisher - NEXT = " + value);
			}).doOnComplete(() -> {
				this.logger.debug("===> ENGINE - Flux.from.nextFigurePublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> ENGINE - Flux.from.nextFigurePublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> ENGINE - Flux.from.nextFigurePublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - Flux.from.nextFigurePublisher =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - Flux.from.nextFigurePublisher =", e);
				return Flux.<Message>empty();
			});
			return fluxOfNextFigure.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.<Message>empty();
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
		return optional.isPresent() ? Mono.just(optional.get()).timeout(Duration.ofSeconds(3))
				: Mono.just(false).timeout(Duration.ofSeconds(3));
	}

}