package org.jmedina.jtetris.figures.service.impl;

import org.jmedina.jtetris.figures.enumeration.FiguraEnumeration;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.figure.Ele;
import org.jmedina.jtetris.figures.figure.Figure;
import org.jmedina.jtetris.figures.repository.FigureRepository;
import org.jmedina.jtetris.figures.service.FigureService;
import org.jmedina.jtetris.figures.util.RandomUtil;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service("figureService")
public class FigureServiceImpl implements FigureService, ApplicationListener<ContextRefreshedEvent> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureRepository figureRepository;
	private final KafkaServiceImpl kafkaService;
	private final SerializeUtil serializeUtil;
	private final RandomUtil random;

	@Value("${figures.topic.nextFigure}")
	public String nextFigureTopic;

	@Override
	public boolean askForNextFigure() throws ServiceException {
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
		return true;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.logger.debug("==> FigureService.onApplicationEvent()");
		this.loadDB();
	}

	private void loadDB() {
		this.logger.debug("==> FigureService.loadDB()");
		this.figureRepository.findAll()
				.forEach(f -> FiguraEnumeration.valueOf(f.getName()).loadCoordinates(f.getBoxes()));
	}

}