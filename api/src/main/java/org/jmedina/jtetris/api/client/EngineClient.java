package org.jmedina.jtetris.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Jorge Medina
 *
 */
@FeignClient(name = "engineClient", url = "${api.engine.base-url}", fallback = EngineFallback.class)
public interface EngineClient {

	@GetMapping(value = "/start", produces = "application/json")
	public Boolean start();

	@GetMapping(value = "/moveRight", produces = "application/json")
	public Boolean moveRight();

	@GetMapping(value = "/moveLeft", produces = "application/json")
	public Boolean moveLeft();

	@GetMapping(value = "/moveDown", produces = "application/json")
	public Boolean moveDown();

}