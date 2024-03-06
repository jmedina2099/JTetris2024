package org.jmedina.jtetris.figures.service.impl;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.jmedina.jtetris.figures.service.KafkaService;
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
public class KafkaServiceImpl implements KafkaService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void sendMessage(String message, String topic) {
		this.logger.debug("==> KafkaService.sendMessage() = {}", message);
		CompletableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(topic, message);
		future.whenComplete((result, ex) -> {
			if (Objects.nonNull(ex)) {
				this.logger.error("=*=> Unable to send message=[{}] due to : {}", message, ex.getMessage());
			}
		});
	}

}