package org.jmedina.jtetris.figures.handler;

import java.util.HashMap;
import java.util.Map;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(value = { Exception.class, RuntimeException.class, ServiceException.class } )
	public Mono<ResponseEntity<Map<String, Object>>> handleException(Exception ex) {
		this.logger.error("=*=> ERROR !!! = {}", ex.getMessage() );
		Map<String, Object> body = new HashMap<>();
		body.put("message", "An error occurred");
		return Mono.just(new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR));
	}
}
