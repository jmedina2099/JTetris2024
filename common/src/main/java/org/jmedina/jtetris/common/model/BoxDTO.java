package org.jmedina.jtetris.common.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
@JsonPropertyOrder({ "x", "y" })
public class BoxDTO implements Box {

	private double x;
	private double y;

	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + ")";
	}

}
