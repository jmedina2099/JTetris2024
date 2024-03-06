package org.jmedina.jtetris.api.client;

import org.springframework.stereotype.Component;

/**
 * @author Jorge Medina
 *
 */
@Component
public class EngineFallback implements EngineClient {

	@Override
	public Boolean start() {
		return false;
	}

	@Override
	public Boolean moveRight() {
		return false;
	}

	@Override
	public Boolean moveLeft() {
		return false;
	}

	@Override
	public Boolean moveDown() {
		return false;
	}

}