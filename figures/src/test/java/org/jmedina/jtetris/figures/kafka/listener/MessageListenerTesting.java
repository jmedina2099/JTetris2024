package org.jmedina.jtetris.figures.kafka.listener;

import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jorge Medina
 *
 */
@Getter
@Setter
public class MessageListenerTesting {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private String message;

	private CountDownLatch latch = new CountDownLatch(1);

	public void listenGroupFigureMessageTest(ConsumerRecord<String, String> record) {
		this.logger.debug("==> KafkaHelperTesting.listenGroupFigureMessageTest = {} {}", record.value(), this);
		this.message = record.value();
		this.latch.countDown();
	}

	public void resetLatch() {
		this.latch = new CountDownLatch(1);
	}

	public void resetMessageListener() {
		this.message = null;
		this.resetLatch();
	}

}