package org.jmedina.jtetris.figures.service;

/**
 * @author Jorge Medina
 *
 */
public interface KafkaService {

	public void sendMessage(String message, String topic);
}
