package org.jmedina.jtetris.engine.model;

import java.util.List;

import org.jmedina.jtetris.engine.figure.Box;

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
public class Board {

	private List<Box> boxes;
	private long timeStamp;
}
