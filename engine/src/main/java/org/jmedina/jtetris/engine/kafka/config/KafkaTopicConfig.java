package org.jmedina.jtetris.engine.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * @author Jorge Medina
 *
 */
@Configuration
public class KafkaTopicConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Value("${engine.topic.nextFigure}")
	private String nextFigureTopic;
	
	@Value("${engine.topic.figure}")
	private String figureTopic;
	
	@Value("${engine.topic.board}")
	private String boardTopic;
	
	@Bean
	KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		return new KafkaAdmin(configs);
	}

	@Bean
	NewTopic nextFigureTopic() {
		return new NewTopic(this.nextFigureTopic, 1, (short) 1);
	}

	@Bean
	NewTopic figureTopic() {
		return new NewTopic(this.figureTopic, 1, (short) 1);
	}

	@Bean
	NewTopic boardTopic() {
		return new NewTopic(this.boardTopic, 1, (short) 1);
	}
}