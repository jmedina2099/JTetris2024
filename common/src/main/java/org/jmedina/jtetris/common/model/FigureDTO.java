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
public class FigureDTO<T extends BoxDTO> implements Figure<T> {

	private List<T> boxes = new ArrayList<>();

}