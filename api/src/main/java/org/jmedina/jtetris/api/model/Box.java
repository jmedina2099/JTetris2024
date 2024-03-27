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
@JsonPropertyOrder({ "x", "y", "initialTimeStamp", "timeStamp" })
public class Box {

	private double x;
	private double y;
	private long initialTimeStamp;
	private long timeStamp;
}
