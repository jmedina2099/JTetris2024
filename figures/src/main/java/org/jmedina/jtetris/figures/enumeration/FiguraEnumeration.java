package org.jmedina.jtetris.figures.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;

/**
 * @author Jorge Medina
 *
 */
@Getter
public enum FiguraEnumeration {

	CAJA(1), ELE(2);

	private int id;

	private final List<Pair<Integer, Integer>> tuplas = new ArrayList<>();

	FiguraEnumeration(int id) {
		this.id = id;
	}

	public void loadCoordinates(String coords) {
		if (this.tuplas.isEmpty()) {
			Arrays.stream(coords.split("-")).forEach(coord -> {
				String[] xy = coord.replaceFirst("\\(", "").replaceFirst("\\)", "").split(",");
				this.tuplas.add(Pair.of(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
			});
		}
	}
}