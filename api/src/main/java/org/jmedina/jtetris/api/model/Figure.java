package org.jmedina.jtetris.api.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@Data
public class Figure implements Cloneable {

	private List<Box> boxes = new ArrayList<>();
	private long initialTimeStamp;
	private long timeStamp;
}