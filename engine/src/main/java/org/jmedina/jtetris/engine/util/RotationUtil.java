package org.jmedina.jtetris.engine.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jmedina.jtetris.engine.figure.Box;
import org.jmedina.jtetris.engine.figure.Figure;

/**
 * @author Jorge Medina
 *
 */
public class RotationUtil {

	public static Figure rotateFigure(Figure figure,int direction) {
		Figure tmpFigure = figure.clone();
		List<Box> boxes = tmpFigure.getBoxes();
		
		List<Point2D.Double> listCoords = boxes.stream().map(box -> new Point2D.Double(box.getX(), box.getY()))
				.collect(Collectors.toList());

		Point2D.Double[] srcPts = listCoords.toArray(new Point2D.Double[0]);
		rotateCoords(srcPts,figure.getCenter(),direction);
		
		List<Point2D.Double> coordsRotated = Arrays.asList( srcPts );
		List<Box> boxesRotated = coordsRotated.stream().map( c -> new Box(c.getX(),c.getY()) ).collect( Collectors.toList() );

		return new Figure(boxesRotated,figure.getCenter(),tmpFigure.getNumRotations(),tmpFigure.getRotation());
	}

	private static void rotateCoords(Point2D.Double[] srcPts, Point2D.Double center, int direction) {

		AffineTransform translate1 = AffineTransform.getTranslateInstance(-center.getX(), -center.getY());
		AffineTransform rotate = AffineTransform.getRotateInstance(direction * Math.toRadians(90d));
		AffineTransform translate2 = AffineTransform.getTranslateInstance(center.getX(), center.getY());

		translate2.concatenate(rotate);
		translate2.concatenate(translate1);
		translate2.transform(srcPts, 0, srcPts, 0, srcPts.length);
	}

}