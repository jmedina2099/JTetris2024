package org.jmedina.jtetris.figures.service.impl;

import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.domain.Figura;
import org.jmedina.jtetris.figures.enumeration.FiguraEnumeration;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.figure.Ele;
import org.jmedina.jtetris.figures.figure.Figure;
import org.jmedina.jtetris.figures.figure.Te;
import org.jmedina.jtetris.figures.figure.Vertical;
import org.jmedina.jtetris.figures.repository.FigureRepository;
import org.jmedina.jtetris.figures.service.FigureService;
import org.jmedina.jtetris.figures.service.FigureTemplateOperations;
import org.jmedina.jtetris.figures.util.RandomUtil;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service("figureService")
public class FigureServiceImpl implements FigureService, ApplicationListener<ContextRefreshedEvent> {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final FigureTemplateOperations figureTemplateOperations;
	private final FigureRepository figureRepository;
	private final KafkaServiceImpl kafkaService;
	private final SerializeUtil serializeUtil;
	private final RandomUtil random;

	@Value("${figures.topic.nextFigure}")
	private String nextFigureTopic;

	@Value("classpath:data/initialData.json")
	private Resource initialDataResource;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.logger.debug("==> FigureService.onApplicationEvent()");
		this.loadFigurasFromDB();
	}

	@Override
	public Figure askForNextFigure() throws ServiceException {
		this.logger.debug("==> FigureService.getNextFigure()");
		Figure figure = null;
		int value = this.random.nextInt(FiguraEnumeration.values().length);
		switch (value) {
		case 0:
			figure = new Caja();
			break;
		case 1:
			figure = new Vertical();
			break;
		case 2:
			figure = new Ele();
			break;
		case 3:
			figure = new Te();
			break;
		default:
			throw new ServiceException(new IllegalArgumentException("Unexpected value: " + value));
		}
		this.kafkaService.sendMessage(this.serializeUtil.convertFigureToString(figure), this.nextFigureTopic);
		return figure;
	}

	@Override
	public void loadFigurasFromDB() {
		this.logger.debug("==> FigureService.loadFigurasFromDB()");
		Flux<Figura> listFigures = this.figureTemplateOperations.findAll();
		listFigures.subscribe(f -> loadFigureCoordinates(f, 1));
		if (Objects.isNull(listFigures.blockFirst())) {
			this.logger.debug("=======> loading initial data...");
			saveFiguras(generateFigurasFromString(linesFromResource(this.initialDataResource)));
		}
		this.logger.debug("==> FigureService.loadFigurasFromDB() ==> DONE");
	}

	private void saveFiguras(List<Figura> figuras) {
		this.figureRepository.saveAll(Flux.just(figuras.toArray(new Figura[0])))
				.subscribe(f -> loadFigureCoordinates(f, 2));
	}

	private void loadFigureCoordinates(Figura f, int id) {
		this.logger.debug("====> loading Figuras ({}) ==> {}", id, f);
		FiguraEnumeration.valueOf(f.getName()).loadFigura(f);
	}

	private List<String> linesFromResource(Resource input) {
		List<String> list = null;
		try {
			list = Files.readAllLines(input.getFile().toPath());
		} catch (Exception e) {
			this.logger.error("=*=> Error trying to read the resource {} = {}", input, e);
		}
		return list;
	}

	private List<Figura> generateFigurasFromString(List<String> lines) {
		return lines.stream().map(line -> serializeUtil.convertStringToFigure(line, Figura.class))
				.collect(Collectors.toList());
	}

}