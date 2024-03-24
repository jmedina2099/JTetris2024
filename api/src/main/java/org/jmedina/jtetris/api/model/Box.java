package org.jmedina.jtetris.api.model;

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
public class Box {

	private double x;
	private double y;
}
