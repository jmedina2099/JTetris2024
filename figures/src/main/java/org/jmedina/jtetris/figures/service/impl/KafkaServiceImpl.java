package org.jmedina.jtetris.figures.service.impl;

import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.service.KafkaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
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
public class KafkaServiceImpl implements KafkaService, ApplicationListener<ContextRefreshedEvent> {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

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

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.logger.debug("==> KafkaService.onApplicationEvent()");
		Properties props = new Properties();
		props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);

		AdminClient adminClient = AdminClient.create(props);
		DescribeClusterResult describeClusterResult = adminClient.describeCluster();
		try {
			Collection<Node> clusterDetails = describeClusterResult.nodes().get();
			clusterDetails.stream().forEach(n -> {
				this.logger.debug("==> kafka node = {}", n);
			});
		} catch (InterruptedException e) {
			this.logger.error("=*=> ERROR initializing kafka", e);
		} catch (ExecutionException e) {
			this.logger.error("=*=> ERROR initializing kafka", e);
		}
	}
}