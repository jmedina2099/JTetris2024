package org.jmedina.jtetris.engine.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class KafkaService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String message) {
		this.logger.debug("==> KafkaService.sendMessage() = {}", message);
		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("figureTopic", message);
		future.whenComplete((result, ex) -> {
			if (ex == null) {
				this.logger.debug("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
			} else {
				this.logger.error("Unable to send message=[{}] due to : {}", message, ex.getMessage());
			}
		});
	}

}