package org.jmedina.jtetris.figures.publisher;

import org.jmedina.jtetris.figures.model.FigureOperation;
import org.jmedina.jtetris.figures.service.FigureService;
import org.reactivestreams.Subscriber;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class FigurePublisher extends CustomPublisher<FigureOperation> {

	@Autowired
	private FigureService figureService;

	public FigurePublisher() {
		super(LoggerFactory.getLogger(FigurePublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
		askForNextFigureOperation();
	}

	public void askForNextFigureOperation() {
		this.logger.debug("===> FigurePublisher.getNextFigure()");
		super.addToQueue(this.figureService.askForNextFigureOperation());
	}
}
