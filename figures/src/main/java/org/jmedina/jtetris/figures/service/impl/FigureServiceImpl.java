package org.jmedina.jtetris.figures.service.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.domain.Figura;
import org.jmedina.jtetris.figures.enumeration.FiguraEnumeration;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.figure.Ele;
import org.jmedina.jtetris.figures.figure.Figure;
import org.jmedina.jtetris.figures.repository.FigureRepository;
import org.jmedina.jtetris.figures.service.FigureService;
import org.jmedina.jtetris.figures.service.FigureTemplateOperations;
import org.jmedina.jtetris.figures.util.RandomUtil;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
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
	public String nextFigureTopic;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.logger.debug("==> FigureService.onApplicationEvent()");
		this.loadFigurasFromDB();
	}

	@Override
	public void askForNextFigure() throws ServiceException {
		this.logger.debug("==> FigureService.getNextFigure()");
		Figure figure = null;
		int value = this.random.nextInt(2);
		switch (value) {
		case 0:
			figure = new Caja();
			break;
		case 1:
			figure = new Ele();
			break;
		default:
			throw new ServiceException(new IllegalArgumentException("Unexpected value: " + value));
		}
		this.kafkaService.sendMessage(this.serializeUtil.convertFigureToString(figure), this.nextFigureTopic);
	}

	@Override
	public void loadFigurasFromDB() {
		this.logger.debug("==> FigureService.loadFigurasFromDB()");
		AtomicBoolean hasElements = new AtomicBoolean(false);
		Flux<Figura> listFigures = this.figureTemplateOperations.findAll();
		listFigures.subscribe(f -> {
			this.logger.debug("====> loading Figuras (1) ==> {}", f);
			hasElements.set(true);
			FiguraEnumeration.valueOf(f.getName()).loadCoordinates(f.getBoxes());
		});
		listFigures.blockLast();
		if (!hasElements.get()) {
			this.logger.debug("==> loading initial data...");
			Flux<Figura> flux = this.figureRepository.saveAll(Flux.just(new Figura(null, "CAJA", "(0,0)-(0,1)-(1,0)-(1,1)"),
													new Figura(null, "ELE", "(0,0)-(0,1)-(0,2)-(0,3)")));
			flux.subscribe(f -> {
				this.logger.debug("====> loading Figuras (2) ==> {}", f);
				FiguraEnumeration.valueOf(f.getName()).loadCoordinates(f.getBoxes());
			});
		}
		this.logger.debug("==> FigureService.loadFigurasFromDB() ==> DONE");
	}

}