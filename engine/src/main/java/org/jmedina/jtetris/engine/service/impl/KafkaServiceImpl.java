package org.jmedina.jtetris.engine.service.impl;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.engine.service.KafkaService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "use.kafka", havingValue = "true")
public class KafkaServiceImpl implements KafkaService {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void sendMessageFigure(String message) {
		this.logger.debug("==> KafkaService.sendMessageFigure() = {}", message);
		sendMessage(message, "figureTopic");
	}

	@Override
	public void sendMessageBoard(String message) {
		this.logger.debug("==> KafkaService.sendMessageBoard() = {}", message);
		sendMessage(message, "boardTopic");
	}

	private void sendMessage(String message, String topic) {
		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
		future.whenComplete((result, ex) -> {
			if (ex == null) {
				this.logger.debug("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
			} else {
				this.logger.error("Unable to send message=[{}] due to : {}", message, ex.getMessage());
			}
		});
	}
}