package org.jmedina.jtetris.figures.kafka.listener;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jorge Medina
 *
 */
@Getter
@Setter
@Component("messageListener")
public class MessageListenerTesting {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private String message;

	private CountDownLatch latch = new CountDownLatch(1);

	@KafkaListener(topics = "${figures.topic.nextFigure}", groupId = "${figures.groupId.messageTest}")
	public void listenGroupFigureMessageTest(String message) {
		this.logger.debug("==> MessageListener.listenGroupFigureMessageTest = {} {}", message, this);
		this.message = message;
		this.latch.countDown();
	}

	public void resetLatch() {
		this.latch = new CountDownLatch(1);
	}
}