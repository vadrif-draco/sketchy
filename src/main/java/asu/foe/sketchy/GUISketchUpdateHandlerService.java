package asu.foe.sketchy;

import org.springframework.stereotype.Component;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

@Component
public class GUISketchUpdateHandlerService {

	// For FREEHAND mode
	private Polyline currentPolyline;

	// For LINE mode
	private Line currentLine;

	// For RECTANGLE mode (also OCR mode)
	private Rectangle currentRectangle;
	private double currentRectangleX;
	private double currentRectangleY;

	// For ELLIPSE mode
	private Ellipse currentEllipse;
	private double currentEllipseX;
	private double currentEllipseY;

	// For ERASER mode
	private Node nodeToErase;

	void handleMousePress(GUISketchScene sketch, Pen pen, double x, double y) {
		switch (pen.getDrawingMode()) {
		case FREEHAND:
			currentPolyline = new Polyline();
			currentPolyline.getPoints().addAll(new Double[] { x, y });
			currentPolyline.setStroke(pen.strokeColorAsJavaFXColor());
			currentPolyline.setStrokeWidth(pen.getStrokeWidth());
			sketch.shapesPane.getChildren().add(currentPolyline);
			break;
		case LINE:
			currentLine = new Line();
			currentLine.setStartX(x);
			currentLine.setStartY(y);
			currentLine.setEndX(x);
			currentLine.setEndY(y);
			currentLine.setStroke(pen.strokeColorAsJavaFXColor());
			currentLine.setStrokeWidth(pen.getStrokeWidth());
			sketch.shapesPane.getChildren().add(currentLine);
			break;
		case RECTANGLE:
			currentRectangleX = x;
			currentRectangleY = y;
			currentRectangle = new Rectangle();
			currentRectangle.setX(currentRectangleX);
			currentRectangle.setY(currentRectangleY);
			currentRectangle.setFill(Color.TRANSPARENT);
			currentRectangle.setStroke(pen.strokeColorAsJavaFXColor());
			currentRectangle.setStrokeWidth(pen.getStrokeWidth());
			sketch.shapesPane.getChildren().add(currentRectangle);
			break;
		case ELLIPSE:
			currentEllipseX = x;
			currentEllipseY = y;
			currentEllipse = new Ellipse();
			currentEllipse.setCenterX(currentEllipseX);
			currentEllipse.setCenterY(currentEllipseY);
			currentEllipse.setFill(Color.TRANSPARENT);
			currentEllipse.setStroke(pen.strokeColorAsJavaFXColor());
			currentEllipse.setStrokeWidth(pen.getStrokeWidth());
			sketch.shapesPane.getChildren().add(currentEllipse);
			break;
		case ERASER:
			break;
		case OCR:
			currentRectangleX = x;
			currentRectangleY = y;
			currentRectangle = new Rectangle();
			currentRectangle.setX(currentRectangleX);
			currentRectangle.setY(currentRectangleY);
			currentRectangle.setFill(Color.rgb(233, 246, 254, 0.5));
			currentRectangle.getStrokeDashArray().addAll(32d, 8d);
			currentRectangle.setStroke(Color.rgb(0, 0, 0, 0.9));
			currentRectangle.setStrokeType(StrokeType.OUTSIDE);
			currentRectangle.setStrokeWidth(0.5);
			sketch.shapesPane.getChildren().add(currentRectangle);
			break;
		default:
			break;
		}
	}

	void handleMouseDrag(GUISketchScene sketch, Pen pen, double x, double y) {
		switch (pen.getDrawingMode()) {
		case FREEHAND:
			currentPolyline.getPoints().addAll(new Double[] { x, y });
			break;
		case LINE:
			currentLine.setEndX(x);
			currentLine.setEndY(y);
			break;
		case RECTANGLE:
			currentRectangle.setX(Math.min(currentRectangleX, x));
			currentRectangle.setY(Math.min(currentRectangleY, y));
			currentRectangle.setWidth(Math.abs(currentRectangleX - x));
			currentRectangle.setHeight(Math.abs(currentRectangleY - y));
			break;
		case ELLIPSE:
			currentEllipse.setCenterX((currentEllipseX + x) / 2);
			currentEllipse.setCenterY((currentEllipseY + y) / 2);
			currentEllipse.setRadiusX(Math.abs(currentEllipseX - x) / 2);
			currentEllipse.setRadiusY(Math.abs(currentEllipseY - y) / 2);
			break;
		case ERASER:
			// Go through all shapes to see if we're intersecting with any
			// Priority goes to smaller shapes in case of intersection with multiple shapes
			Node intersectingNode = null;
			for (Node node : sketch.shapesPane.getChildren()) {
				if (node.getBoundsInParent().intersects(x, y, 0, 0)) {
					if (intersectingNode == null || node.computeAreaInScreen() < intersectingNode.computeAreaInScreen())
						intersectingNode = node;
				}
			}
			// If the currently intersecting node is different from the one marked for deletion...
			if (intersectingNode != nodeToErase) {
				if (nodeToErase != null) nodeToErase.setOpacity(1.0); // Unmark it
				nodeToErase = intersectingNode; // Set the newly intersecting node as the node to erase
				if (nodeToErase != null) nodeToErase.setOpacity(0.1); // Now mark the newly intersecting node (if exists)
			}
			break;
		case OCR:
			currentRectangle.setX(Math.min(currentRectangleX, x));
			currentRectangle.setY(Math.min(currentRectangleY, y));
			currentRectangle.setWidth(Math.abs(currentRectangleX - x));
			currentRectangle.setHeight(Math.abs(currentRectangleY - y));
		default:
			break;
		}
	}

	void handleMouseRelease(GUISketchScene sketch, Pen pen, double x, double y) {
		switch (pen.getDrawingMode()) {
		case ERASER:
			sketch.shapesPane.getChildren().remove(nodeToErase);
			nodeToErase = null;
			break;
		case OCR:
			sketch.shapesPane.getChildren().remove(currentRectangle);
			SnapshotParameters params = new SnapshotParameters();
			params.setViewport(new Rectangle2D(
						currentRectangle.getX(),
						currentRectangle.getY(),
						currentRectangle.getWidth(),
						currentRectangle.getHeight()));
			WritableImage imgReturn = sketch.shapesPane.snapshot(params, null);
			sketch.shapesPane.getChildren().add(currentRectangle);
			// TODO: Apply OCR on imgReturn then present a dialog with text OCR'ed
			sketch.shapesPane.getChildren().remove(currentRectangle);
			break;
		default:
			break;
		}
	}
}
