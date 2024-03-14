package org.jmedina.jtetris.engine.figure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@Data
public class Figure implements Cloneable {
	
	@JsonIgnore
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<Box> boxes = new ArrayList<>();
	public Point center;
	public int numRotations;
	
	@JsonIgnore
	public int rotation = 0;
	
	public Figure( List<Box> boxes,Point center, int numRotations, int rotation ) {
		this.boxes = boxes;
		this.center = center;
		this.numRotations = numRotations;
		this.rotation = rotation;
	}

	public boolean moveRight() {
		this.logger.debug("==> moveRight = {}",center);
		this.center.x += Box.SIZE;
		return this.boxes.stream().allMatch(Box::moveRight);
	}

	public boolean moveLeft() {
		this.logger.debug("==> moveLeft = {}",center);
		this.center.x -= Box.SIZE;
		return this.boxes.stream().allMatch(Box::moveLeft);
	}

	public boolean moveDown() {
		this.logger.debug("==> moveDown = {}",center);
		this.center.y += Box.SIZE;
		return this.boxes.stream().allMatch(Box::moveDown);
	}

	@Override
	public Figure clone() {
		List<Box> list = this.boxes.stream().map( b -> b.clone() ).collect( Collectors.toList() );
		Point center = new Point(this.center.x,this.center.y);
		return new Figure(list,center,numRotations,rotation);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.boxes.stream().forEach(b -> sb.append(b.toString()));
		return sb.toString();
	}

}