package org.jmedina.jtetris.figures.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
@EnableMongoRepositories
public class MongoConfig extends AbstractReactiveMongoConfiguration {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${figures.mongodb.name}")
	private String mongoDbName;

	@Override
	protected String getDatabaseName() {
		return this.mongoDbName;
	}

	@Override
	@Bean
	public MongoClient reactiveMongoClient() {
		this.logger.debug("==> FiguresApplication.mongoClient()");
		return MongoClients.create("mongodb://localhost:27017/" + this.mongoDbName);
	}
}