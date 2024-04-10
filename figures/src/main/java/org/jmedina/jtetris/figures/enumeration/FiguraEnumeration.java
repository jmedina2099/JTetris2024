package org.jmedina.jtetris.figures.enumeration;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jmedina.jtetris.figures.domain.Figura;

import lombok.Getter;

/**
 * @author Jorge Medina
 *
 */
@Getter
public enum FiguraEnumeration {

	VERTICAL(1), ELE(2), TE(3), CAJA(4);

	private int id;

	private final List<Pair<Integer, Integer>> tuplas = new ArrayList<>();

	private final Point2D.Double center = new Point2D.Double();

	private int numRotations;

	FiguraEnumeration(int id) {
		this.id = id;
	}

	public void loadFigura(Figura f) {
		this.tuplas.clear();
		String coords = f.getBoxes();
		Arrays.stream(coords.split("-")).forEach(coord -> {
			String[] xy = coord.replaceFirst("\\(", "").replaceFirst("\\)", "").split(",");
			this.tuplas.add(Pair.of(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
		});
		String center = f.getCenter();
		String[] xy = center.split(",");
		this.center.x = Double.parseDouble(xy[0]);
		this.center.y = Double.parseDouble(xy[1]);
		this.numRotations = f.getNumRotations();
	}
}