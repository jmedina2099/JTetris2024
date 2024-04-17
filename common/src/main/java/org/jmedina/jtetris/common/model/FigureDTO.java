package org.jmedina.jtetris.common.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FigureDTO implements Figure {

	private List<BoxDTO> boxes = new ArrayList<>();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.boxes.stream().forEach(b -> sb.append(b.toString()));
		return sb.toString();
	}

}