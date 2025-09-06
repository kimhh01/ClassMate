package com.example.navermapsample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

public class PathDrawer {

    public static void drawPath(Bitmap bitmap, List<Node> path) {
        if (path == null || path.isEmpty()) return;

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);

        Canvas canvas = new Canvas(bitmap);

        Node from = path.get(0);
        canvas.drawCircle(from.x, from.y, 15, paint); // Start point

        for (int i = 1; i < path.size(); i++) {
            Node to = path.get(i);
            float distance = (float) Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
            if (distance >= 20) {
                canvas.drawLine(from.x, from.y, to.x, to.y, paint);
                from = to;
                canvas.drawCircle(from.x, from.y, 15, paint); // Mark the node
            }
        }

        Node end = path.get(path.size() - 1);
        canvas.drawCircle(end.x, end.y, 15, paint); // End point
    }

    public static void drawPoint(Bitmap bitmap, int x, int y, int color, int radius) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(x, y, radius, paint);
    }
}
