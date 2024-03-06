package org.jmedina.jtetris.figures.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jorge Medina
 *
 */
@Component
@Getter
@Setter
public class MessageListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String message;

	@KafkaListener(topics = "${figures.topic.nextFigure}", groupId = "${figures.groupId.messageTest}")
	public void listenGroupFigureMessageTest(String message) {
		this.logger.debug("==> MessageListener.listenGroupFigureMessageTest = {}", message);
		this.message = message;
	}

}
