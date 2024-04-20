package org.jmedina.jtetris.engine.controller;

import java.security.Principal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Quintet;
import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.engine.client.FiguresClient;
import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.jmedina.jtetris.engine.figure.FigureMotion;
import org.jmedina.jtetris.engine.model.Message;
import org.jmedina.jtetris.engine.model.NextFigureOperation;
import org.jmedina.jtetris.engine.publisher.BoardPublisher;
import org.jmedina.jtetris.engine.publisher.EnginePublisher;
import org.jmedina.jtetris.engine.publisher.FigurePublisher;
import org.jmedina.jtetris.engine.publisher.NextFigurePublisher;
import org.jmedina.jtetris.engine.service.ConversationService;
import org.jmedina.jtetris.engine.service.EngineService;
import org.jmedina.jtetris.engine.service.impl.EngineServiceImpl;
import org.jmedina.jtetris.engine.util.RotationUtil;
import org.jmedina.jtetris.engine.util.SerializeUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EngineController {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final ConversationService conversationService;
	private final FiguresClient figuresClient;
	private final RotationUtil rotationUtil;
	private final SerializeUtil serializeUtil;
	private final boolean showHeaders = false;

	private final Map<String, Quintet<FigurePublisher, EnginePublisher, BoardPublisher, NextFigurePublisher, EngineService>> mapSession = new HashMap<>();

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

	@GetMapping(value = "/isUp", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> isUp(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.isUp()");
		this.logger.debug("===> ENGINE - USERNAME = {}", Objects.nonNull(user) ? user.getName() : user);
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		try {
			return this.figuresClient.isUp(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.empty();
		}
	}

	@PostMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> start(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.start()");
		String auth = getAuthHeader(exchange);
		try {
			if (Objects.nonNull(user)) {
				EngineService engineService = new EngineServiceImpl(this.rotationUtil, this.serializeUtil);
				mapSession.put(user.getName(),
						Quintet.with(new FigurePublisher(auth, engineService, this.conversationService),
								new EnginePublisher(), new BoardPublisher(), new NextFigurePublisher(), engineService));
			}
			return this.figuresClient.start(auth).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.stop()");
		try {
			if (Objects.nonNull(user)) {
				Quintet<FigurePublisher, EnginePublisher, BoardPublisher, NextFigurePublisher, EngineService> session = mapSession
						.get(user.getName());
				if (Objects.nonNull(session)) {
					session.getValue0().stop();
					session.getValue1().stop();
					session.getValue2().stop();
					session.getValue3().stop();
					session.getValue4().stop();
				}
			}
			return this.figuresClient.stop(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>> getFigureConversation(Principal user,
			ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.getFigureConversation()");
		this.logger.debug("===> ENGINE - USERNAME = {}", Objects.nonNull(user) ? user.getName() : "");
		FigurePublisher figurePublisher = null;
		EnginePublisher enginePublisher = null;
		BoardPublisher boardPublisher = null;
		NextFigurePublisher nextFigurePublisher = null;
		EngineService engineService = null;
		if (Objects.nonNull(user)) {
			Quintet<FigurePublisher, EnginePublisher, BoardPublisher, NextFigurePublisher, EngineService> session = mapSession
					.get(user.getName());
			if (Objects.nonNull(session)) {
				figurePublisher = session.getValue0();
				enginePublisher = session.getValue1();
				boardPublisher = session.getValue2();
				nextFigurePublisher = session.getValue3();
				engineService = session.getValue4();
			}
		}
		engineService.start(nextFigurePublisher, enginePublisher, boardPublisher);
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		Flux<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>> fluxFromFigures = null;
		Flux<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>> fluxFromEngine = null;
		try {
			fluxFromFigures = Flux.from(figurePublisher).doOnNext(figure -> {
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
				return Flux.<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>>empty();
			});
			fluxFromEngine = Flux.from(enginePublisher).doOnNext(figure -> {
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
				return Flux.<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>>empty();
			});
			return fluxFromFigures.mergeWith(fluxFromEngine).timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>>empty();
		}
	}

	@GetMapping(value = "/getBoardConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<BoardOperation<BoxMotion>> getBoardConversation(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.getBoardConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		BoardPublisher boardPublisher = null;
		if (Objects.nonNull(user)) {
			Quintet<FigurePublisher, EnginePublisher, BoardPublisher, NextFigurePublisher, EngineService> session = mapSession
					.get(user.getName());
			if (Objects.nonNull(session)) {
				boardPublisher = session.getValue2();
			}
		}
		Flux<BoardOperation<BoxMotion>> fluxOfBoards = null;
		try {
			fluxOfBoards = Flux.from(boardPublisher).doOnNext(figure -> {
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
				return Flux.<BoardOperation<BoxMotion>>empty();
			});
			return fluxOfBoards.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.<BoardOperation<BoxMotion>>empty();
		}
	}

	@GetMapping(value = "/getNextFigureConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<NextFigureOperation> getNextFigureConversation(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.getNextFigureConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		NextFigurePublisher nextFigurePublisher = null;
		if (Objects.nonNull(user)) {
			Quintet<FigurePublisher, EnginePublisher, BoardPublisher, NextFigurePublisher, EngineService> session = mapSession
					.get(user.getName());
			if (Objects.nonNull(session)) {
				nextFigurePublisher = session.getValue3();
			}
		}
		Flux<NextFigureOperation> fluxOfNextFigure = null;
		try {
			fluxOfNextFigure = Flux.from(nextFigurePublisher).doOnNext(value -> {
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
				return Flux.<NextFigureOperation>empty();
			});
			return fluxOfNextFigure.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.<NextFigureOperation>empty();
		}
	}

	@PostMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveRight(Principal user) {
		this.logger.debug("===> EngineController.moveRight()");
		try {
			return convertOptionalToMonoBoolean(getEngineService(user).moveRight());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveLeft(Principal user) {
		this.logger.debug("===> EngineController.moveLeft()");
		try {
			return convertOptionalToMonoBoolean(getEngineService(user).moveLeft());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateRight(Principal user) {
		this.logger.debug("===> EngineController.rotateRight()");
		try {
			return convertOptionalToMonoBoolean(getEngineService(user).rotateRight());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateLeft(Principal user) {
		this.logger.debug("===> EngineController.rotateLeft()");
		try {
			return convertOptionalToMonoBoolean(getEngineService(user).rotateLeft());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> bottomDown(Principal user) {
		this.logger.debug("===> EngineController.bottomDown()");
		try {
			return convertOptionalToMonoBoolean(getEngineService(user).bottomDown());
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	private Mono<Boolean> convertOptionalToMonoBoolean(Optional<Boolean> optional) {
		return optional.isPresent() ? Mono.just(optional.get()).timeout(Duration.ofSeconds(3))
				: Mono.just(false).timeout(Duration.ofSeconds(3));
	}

	private void printHeaders(ServerWebExchange exchange) {
		this.logger.info("=========> HEADERS!!!");
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> keys = headers.keySet();
		keys.stream().forEach(key -> {
			List<String> values = headers.get(key);
			if (values != null) {
				values.stream().forEach(v -> {
					this.logger.info("=========> header= {}: {}", key, v);
				});
			}
		});
		this.logger.info("=========> END HEADERS!!!");
	}

	private String getAuthHeader(ServerWebExchange exchange) {
		List<String> values = exchange.getRequest().getHeaders().get("authorization");
		if (Objects.nonNull(values) && !values.isEmpty()) {
			return values.get(0);
		}
		return null;
	}

	private EngineService getEngineService(Principal user) {
		EngineService engineService = null;
		if (Objects.nonNull(user)) {
			Quintet<FigurePublisher, EnginePublisher, BoardPublisher, NextFigurePublisher, EngineService> session = mapSession
					.get(user.getName());
			if (Objects.nonNull(session)) {
				engineService = session.getValue4();
			}
		}
		return engineService;
	}

}