package org.jmedina.jtetris.engine.service;

/**
 * @author Jorge Medina
 *
 */
public interface KafkaService {

	public void sendMessageFigure(String message);

	public void sendMessageBoard(String message);
}
