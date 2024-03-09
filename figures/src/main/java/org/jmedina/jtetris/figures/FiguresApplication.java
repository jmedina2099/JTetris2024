package org.jmedina.jtetris.figures;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

/**
 * @author Jorge Medina
 *
 */
@EnableReactiveMongoRepositories
@SpringBootApplication
public class FiguresApplication extends AbstractReactiveMongoConfiguration {

	@Value("${figures.mongodb.name}")
	private String mongoDbName;

	@Bean
	MongoClient mongoClient() {
		return MongoClients.create();
	}

	@Override
	protected String getDatabaseName() {
		return mongoDbName;
	}

	public static void main(String[] args) {
		SpringApplication.run(FiguresApplication.class, args);
	}

}