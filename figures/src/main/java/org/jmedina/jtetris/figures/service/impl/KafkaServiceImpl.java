package org.jmedina.jtetris.figures.service.impl;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.service.KafkaService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class KafkaServiceImpl implements KafkaService {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Getter
	private CountDownLatch latchForTesting = new CountDownLatch(1);

	@Override
	public void sendMessage(String message, String topic) throws ServiceException {
		this.logger.debug("==> KafkaService.sendMessage() = {} {}", message, topic);
		CompletableFuture<SendResult<String, String>> future = null;
		try {
			future = this.kafkaTemplate.send(topic, message);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		future.whenComplete((result, ex) -> {
			if (Objects.nonNull(ex)) {
				this.logger.error("=*=> Unable to send message=[{}] due to : {}", message, ex.getMessage());
				this.latchForTesting.countDown();
			}
		});
	}

	public void resetLatchForTesting() {
		this.latchForTesting = new CountDownLatch(1);
	}
}