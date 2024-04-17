package org.jmedina.jtetris.engine.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jmedina.jtetris.engine.figure.BoxForEngine;
import org.jmedina.jtetris.engine.figure.FigureForEngine;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class RotationUtil {

	public void rotateFigure(FigureForEngine figure, int direction) {
		List<BoxForEngine> boxes = figure.getBoxes();

		List<Point2D.Double> listCoords = boxes.stream().map(box -> new Point2D.Double(box.getX(), box.getY()))
				.collect(Collectors.toList());

		Point2D.Double[] srcPts = listCoords.toArray(new Point2D.Double[0]);
		rotateCoords(srcPts, figure.getCenter(), direction);

		List<Point2D.Double> coordsRotated = Arrays.asList(srcPts);
		List<BoxForEngine> boxesRotated = coordsRotated.stream().map(c -> new BoxForEngine(c.getX(), c.getY()))
				.collect(Collectors.toList());

		figure.setBoxes(boxesRotated);
	}

	private void rotateCoords(Point2D.Double[] srcPts, Point2D.Double center, int direction) {

		AffineTransform translate1 = AffineTransform.getTranslateInstance(-center.getX(), -center.getY());
		AffineTransform rotate = AffineTransform.getRotateInstance(direction * Math.toRadians(90d));
		AffineTransform translate2 = AffineTransform.getTranslateInstance(center.getX(), center.getY());

		translate2.concatenate(rotate);
		translate2.concatenate(translate1);
		translate2.transform(srcPts, 0, srcPts, 0, srcPts.length);
	}

}
