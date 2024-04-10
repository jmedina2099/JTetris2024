package org.jmedina.jtetris.figures.figure;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.enumeration.FiguraEnumeration;
import org.jmedina.jtetris.figures.service.FigureTemplateOperations;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class Figure {

	@JsonIgnore
	private final Logger logger = LogManager.getLogger(this.getClass());

	@JsonIgnore
	@EqualsAndHashCode.Exclude
	protected FiguraEnumeration type;

	@JsonIgnore
	protected int id;
	protected List<Box> boxes;
	protected Point2D.Double center = new Point2D.Double();
	public int numRotations;

	protected void init(FiguraEnumeration type) {
		this.type = type;
		this.id = type.getId();
	}

	public Mono<Figure> load(FigureTemplateOperations template) {
		this.logger.debug("==> load = " + this.type.name());
		return template.findByName(this.type.name()).map(fig -> {
			this.logger.debug("==> loading... " + fig);
			this.type.loadFigura(fig);
			this.boxes = new ArrayList<>(
					this.type.getTuplas().stream().map(t -> new Box(t)).collect(Collectors.toList()));
			this.logger.debug("==> boxes... " + this.boxes);
			this.center = type.getCenter();
			this.numRotations = type.getNumRotations();
			return this;
		});
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.boxes.stream().forEach(b -> sb.append(b.toString()));
		return sb.toString();
	}

}