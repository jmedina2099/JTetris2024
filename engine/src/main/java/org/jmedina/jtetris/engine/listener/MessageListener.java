package org.jmedina.jtetris.engine.listener;

import java.util.Optional;

import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.tool.Engine;
import org.jmedina.jtetris.engine.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Component
public class MessageListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SerializeUtil serializeUtil;
	private final Engine engine;

	@KafkaListener(topics = "${engine.topic.nextFigure}", groupId = "${engine.groupId.message}")
	public void listenGroupFigureMessage(String message) {
		this.logger.debug("==> MessageListener.listenGroupFigureMessage() = {} ", message);
		Optional<Figure> optional = this.serializeUtil.convertStringToFigure(message);
		if (optional.isPresent()) {
			this.engine.addFallingFigure(optional.get());
		}
	}
}
