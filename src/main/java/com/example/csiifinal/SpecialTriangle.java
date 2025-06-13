package com.example.csiifinal;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Set;

public class SpecialTriangle {

    public enum TriangleType {
        TRIANGLE_30_60_90,
        TRIANGLE_45_45_90
    }

    private static final Map<Integer, TriangleType> angleToTriangle = Map.ofEntries(
            Map.entry(30, TriangleType.TRIANGLE_30_60_90),
            Map.entry(60, TriangleType.TRIANGLE_30_60_90),
            Map.entry(120, TriangleType.TRIANGLE_30_60_90),
            Map.entry(150, TriangleType.TRIANGLE_30_60_90),
            Map.entry(210, TriangleType.TRIANGLE_30_60_90),
            Map.entry(240, TriangleType.TRIANGLE_30_60_90),
            Map.entry(300, TriangleType.TRIANGLE_30_60_90),
            Map.entry(330, TriangleType.TRIANGLE_30_60_90),
            Map.entry(45, TriangleType.TRIANGLE_45_45_90),
            Map.entry(135, TriangleType.TRIANGLE_45_45_90),
            Map.entry(225, TriangleType.TRIANGLE_45_45_90),
            Map.entry(315, TriangleType.TRIANGLE_45_45_90)
    );

    private static final Set<Integer> ROTATE_CW_90 = Set.of(60, 300);
    private static final Set<Integer> ROTATE_CCW_90 = Set.of(120, 240);

    public static void showTriangleForAngle(int angle) {
        TriangleType type = angleToTriangle.get(angle);
        if (type == null) return;

        int xFlip = -1;
        int yFlip = 1;

        switch (angle) {
            case 30, 45 -> {
                xFlip = -1;
                yFlip = 1;
            }
            case 60 -> {
                xFlip = 1;
                yFlip = 1;
            }
            case 120 -> {
                xFlip = -1;
                yFlip = 1;
            }
            case 135, 150 -> {
                xFlip = 1;
                yFlip = 1;
            }
            case 210, 225 -> {
                xFlip = 1;
                yFlip = -1;
            }
            case 240 -> {
                xFlip = 1;
                yFlip = 1;
            }
            case 300 -> {
                xFlip = -1;
                yFlip = 1;
            }
            case 315, 330 -> {
                xFlip = -1;
                yFlip = -1;
            }
        }

        boolean rotateCW = ROTATE_CW_90.contains(angle);
        boolean rotateCCW = ROTATE_CCW_90.contains(angle);

        Stage popup = new Stage();
        popup.setTitle("Special Triangle: " + angle + "°");

        Canvas canvas = new Canvas(600, 600);
        drawTriangle(canvas, type, xFlip, yFlip, rotateCW, rotateCCW);

        StackPane pane = new StackPane(canvas);
        popup.setScene(new Scene(pane));
        popup.show();
    }

    private static void drawTriangle(Canvas canvas, TriangleType type, int xFlip, int yFlip, boolean rotateCW, boolean rotateCCW) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFont(new Font(16));

        switch (type) {
            case TRIANGLE_30_60_90 -> draw30_60_90(gc, xFlip, yFlip, rotateCW, rotateCCW);
            case TRIANGLE_45_45_90 -> draw45_45_90(gc, xFlip, yFlip, rotateCW, rotateCCW);
        }
    }

    private static Point2D transform(Point2D p, double centerX, double centerY, int xFlip, int yFlip, boolean rotateCW, boolean rotateCCW) {
        // Flip
        double x = xFlip * (p.getX() - centerX);
        double y = yFlip * (p.getY() - centerY);

        // Rotate
        if (rotateCW) {
            double tmp = x;
            x = y;
            y = -tmp;
        } else if (rotateCCW) {
            double tmp = x;
            x = -y;
            y = tmp;
        }

        // Translate back
        return new Point2D(centerX + x, centerY + y);
    }

    private static void draw30_60_90(GraphicsContext gc, int xFlip, int yFlip, boolean rotateCW, boolean rotateCCW) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        double scale = Math.min(width, height) * 0.4;
        double cx = width / 2, cy = height / 2;

        Point2D A = new Point2D(cx - scale / 2, cy + scale / 2);
        Point2D B = new Point2D(A.getX(), A.getY() - scale * 0.5);
        Point2D C = new Point2D(A.getX() + scale * Math.sqrt(3) / 2, A.getY());

        A = transform(A, cx, cy, xFlip, yFlip, rotateCW, rotateCCW);
        B = transform(B, cx, cy, xFlip, yFlip, rotateCW, rotateCCW);
        C = transform(C, cx, cy, xFlip, yFlip, rotateCW, rotateCCW);

        gc.strokeLine(A.getX(), A.getY(), B.getX(), B.getY());
        gc.strokeLine(B.getX(), B.getY(), C.getX(), C.getY());
        gc.strokeLine(C.getX(), C.getY(), A.getX(), A.getY());

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(18));
        // Angle label
        gc.fillText("90°", A.getX() + 10, A.getY() - 10);

// Top-centered text labels
        double labelX = gc.getCanvas().getWidth() / 2 - 100;
        double labelY = 40;
        gc.fillText("30-60-90 Triangle", labelX, labelY);
        gc.fillText("Unit Circle Hypotenuse = 1", labelX, labelY + 25);
        gc.fillText("X = cos(x) = √3/2", labelX, labelY + 50);
        gc.fillText("Y = sin(x) = 1/2", labelX, labelY + 75);

// Leave "r=1" near hypotenuse
        gc.fillText("r=1", ((B.getX() + C.getX()) / 2)-10, (B.getY() + C.getY()) / 2 - 10);
    }

    private static void draw45_45_90(GraphicsContext gc, int xFlip, int yFlip, boolean rotateCW, boolean rotateCCW) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        double scale = Math.min(width, height) * 0.4;
        double cx = width / 2, cy = height / 2;

        Point2D A = new Point2D(cx - scale / 2, cy + scale / 2);
        Point2D B = new Point2D(A.getX(), A.getY() - scale);
        Point2D C = new Point2D(A.getX() + scale, A.getY());

        A = transform(A, cx, cy, xFlip, yFlip, rotateCW, rotateCCW);
        B = transform(B, cx, cy, xFlip, yFlip, rotateCW, rotateCCW);
        C = transform(C, cx, cy, xFlip, yFlip, rotateCW, rotateCCW);

        gc.strokeLine(A.getX(), A.getY(), B.getX(), B.getY());
        gc.strokeLine(B.getX(), B.getY(), C.getX(), C.getY());
        gc.strokeLine(C.getX(), C.getY(), A.getX(), A.getY());

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(18));
        // Angle label
        gc.fillText("90°", A.getX() + 10, A.getY() - 10);

// Top-centered text labels
        double labelX = gc.getCanvas().getWidth() / 2 - 100;
        double labelY = 40;
        gc.fillText("45-45-90 Triangle", labelX, labelY);
        gc.fillText("Unit Circle Hypotenuse = 1", labelX, labelY + 25);
        gc.fillText("X = cos(x) = √2/2", labelX, labelY + 50);
        gc.fillText("Y = sin(x) = √2/2", labelX, labelY + 75);

// Leave "r=1" near hypotenuse
        gc.fillText("r=1", (B.getX() + C.getX()) / 2 + 0, (B.getY() + C.getY()) / 2 - 10);
    }
}
