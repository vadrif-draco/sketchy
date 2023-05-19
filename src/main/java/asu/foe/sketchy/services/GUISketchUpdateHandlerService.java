package asu.foe.sketchy.services;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIPen;
import asu.foe.sketchy.scenes.GUISketchScene;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
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

	public void handleMousePress(GUISketchScene sketch, GUIPen pen, double x, double y) {
		switch (pen.getDrawingMode()) {
		case FREEHAND:
			currentPolyline = new Polyline();
			currentPolyline.getPoints().addAll(new Double[] { x, y });
			currentPolyline.setStroke(pen.strokeColorAsJavaFXColor());
			currentPolyline.setStrokeWidth(pen.getStrokeWidth());
			sketch.getShapesPane().getChildren().add(currentPolyline);
			break;
		case LINE:
			currentLine = new Line();
			currentLine.setStartX(x);
			currentLine.setStartY(y);
			currentLine.setEndX(x);
			currentLine.setEndY(y);
			currentLine.setStroke(pen.strokeColorAsJavaFXColor());
			currentLine.setStrokeWidth(pen.getStrokeWidth());
			sketch.getShapesPane().getChildren().add(currentLine);
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
			sketch.getShapesPane().getChildren().add(currentRectangle);
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
			sketch.getShapesPane().getChildren().add(currentEllipse);
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
			sketch.getShapesPane().getChildren().add(currentRectangle);
			break;
		default:
			break;
		}
	}

	public void handleMouseDrag(GUISketchScene sketch, GUIPen pen, double x, double y) {
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
			for (Node node : sketch.getShapesPane().getChildren()) {
				if (node instanceof HBox) continue; // Skip the HBox, which is the mouse
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

	public void handleMouseRelease(GUISketchScene sketch, GUIPen pen, double x, double y) {
		switch (pen.getDrawingMode()) {
		case ERASER:
			sketch.getShapesPane().getChildren().remove(nodeToErase);
			nodeToErase = null;
			break;
		case OCR:
			sketch.getShapesPane().getChildren().remove(currentRectangle);
			SnapshotParameters params = new SnapshotParameters();
			params.setViewport(new Rectangle2D(
						currentRectangle.getX(),
						currentRectangle.getY(),
						currentRectangle.getWidth(),
						currentRectangle.getHeight()));
			WritableImage imgReturn = sketch.getShapesPane().snapshot(params, null);
			sketch.getShapesPane().getChildren().add(currentRectangle);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("OCR Service");
			alert.setHeaderText("Now performing OCR on your selection...");
			alert.setContentText("Please wait, contacting OCR server!\nThis should only take a few seconds...");
			alert.show();
			Task<Void> ocrTask = new Task<>() {
				@Override
				protected Void call() throws Exception {
					String ocrResponse = null;
					try {
						File tempPngFile = File.createTempFile("temp", ".png");
						ImageIO.write(SwingFXUtils.fromFXImage(imgReturn, null), "png", tempPngFile);
						ocrResponse = OCRService.send(tempPngFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					final String finalOcrResponse = ocrResponse;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (finalOcrResponse != null) {
								alert.setHeaderText("Done! Here's your OCR'ed content...");
								alert.setWidth(480);
								alert.setHeight(360);
								TextArea finalOcrResponseTextArea = new TextArea(finalOcrResponse);
								finalOcrResponseTextArea.setEditable(false);
								finalOcrResponseTextArea.setFocusTraversable(false);
								finalOcrResponseTextArea.setStyle("""
											-fx-font-size: 14pt;
											-fx-text-fill: gray;
											-fx-background-insets: 0;
											-fx-background-color: transparent;
											""");
								alert.getDialogPane().setContent(finalOcrResponseTextArea);
							} else {
								alert.setAlertType(Alert.AlertType.ERROR);
								alert.setHeaderText("Whoops! Try again later?");
								alert.setContentText("So sorry!\nCouldn't perform OCR on your selection...");
							}
							if (!alert.isShowing()) alert.show();
						}
					});
					return null;
				}
			};
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(ocrTask);
			sketch.getShapesPane().getChildren().remove(currentRectangle);
			break;
		default:
			break;
		}
	}
}
