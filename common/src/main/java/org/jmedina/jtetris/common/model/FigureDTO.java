package org.jmedina.jtetris.common.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class FigureDTO implements Figure {

	private List<BoxDTO> boxes = new ArrayList<>();

}