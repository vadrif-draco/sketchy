package asu.foe.sketchy;

import javafx.scene.paint.Color;

public class Pen {

	public enum DrawingMode {
		FREEHAND,
		LINE,
		RECTANGLE,
		ELLIPSE,
		ERASER,
		OCR
	}

	public class MyColor {
		public int r = 0;
		public int g = 0;
		public int b = 0;
		public MyColor() { super(); }
		public MyColor(int r, int g, int b) { this.r = r; this.g = g; this.b = b; }
	}

	private double strokeWidth = 2;
	private MyColor strokeColor = new MyColor(0, 0, 0);
	private DrawingMode drawingMode = DrawingMode.FREEHAND;

	public Pen() { super(); }
	public Pen(double strokeWidth, MyColor strokeColor, DrawingMode drawingMode) {
		super();
		this.strokeWidth = strokeWidth;
		this.strokeColor = strokeColor;
		this.drawingMode = drawingMode;
	}

	public double getStrokeWidth() { return strokeWidth; }
	public void setStrokeWidth(double strokeWidth) { this.strokeWidth = strokeWidth; }
	public MyColor getStrokeColor() { return strokeColor; }
	public void setStrokeColor(MyColor strokeColor) { this.strokeColor = strokeColor; }
	public DrawingMode getDrawingMode() { return drawingMode; }
	public void setDrawingMode(DrawingMode drawingMode) { this.drawingMode = drawingMode; }

	public Color strokeColorAsJavaFXColor() { return Color.rgb(strokeColor.r, strokeColor.g, strokeColor.b); }
	public void strokeColorFromJavaFXColor(Color color) {
		strokeColor.r = (int) (color.getRed() * 255);
		strokeColor.g = (int) (color.getGreen() * 255);
		strokeColor.b = (int) (color.getBlue() * 255);
	}

}
