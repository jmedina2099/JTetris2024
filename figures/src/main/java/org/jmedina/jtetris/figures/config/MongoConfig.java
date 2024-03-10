package org.jmedina.jtetris.figures.config;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.domain.Figura;
import org.jmedina.jtetris.figures.repository.FigureRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import reactor.core.publisher.Flux;

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

	@Bean
	@ConditionalOnProperty(prefix = "figures.mongodb", name = "loadInitial", havingValue = "true")
	CommandLineRunner loadData(FigureRepository figureRepository) {
		AtomicBoolean hasElements = new AtomicBoolean(false);
		Flux<Figura> listFiguras = figureRepository.findAll();
		listFiguras.subscribe(f -> hasElements.set(true));
		listFiguras.blockLast();
		if (!hasElements.get()) {
			this.logger.debug("==> loading data...");
			Figura caja = new Figura(null, "CAJA", "(0,0)-(0,1)-(1,0)-(1,1)");
			Figura ele = new Figura(null, "ELE", "(0,0)-(0,1)-(0,2)-(0,3)");
			return args -> figureRepository.saveAll(Flux.just(caja, ele))
					.subscribe(t -> this.logger.debug("==> fig ==> {}", t));
		}
		return null;
	}
}