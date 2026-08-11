package com.example.navermapsample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.ImageView;

import java.util.List;

public class PathDrawer {

    public static void drawPath(final Bitmap bitmap, final List<Node> path, final ImageView imageView,
                                final Bitmap startGif, final Bitmap destinationGif) {
        if (path == null || path.isEmpty()) return;

        final Paint paint = new Paint();
        paint.setColor(Color.BLUE); // 경로색
        paint.setStyle(Paint.Style.FILL);

        final Canvas canvas = new Canvas(bitmap);
        final Handler handler = new Handler();

        // 출발 지점에 GIF 이미지 그리기
        Node startNode = path.get(0);
        drawGifMarker(canvas, startGif, startNode.x, startNode.y);

        Runnable drawRunnable = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                int batchSize = 10;  // 한 번에 그릴 점의 수
                int endIndex = Math.min(index + batchSize, path.size());

                for (; index < endIndex; index++) {
                    Node node = path.get(index);
                    canvas.drawCircle(node.x, node.y, 8, paint);
                }

                // 도착 지점에 GIF 이미지 표시
                if (index == path.size()) {
                    Node destinationNode = path.get(path.size() - 1);
                    drawGifMarker(canvas, destinationGif, destinationNode.x, destinationNode.y);

                    // 출발 지점 GIF를 다시 그려서 제일 위로 올림
                    Node startNode = path.get(0);
                    drawGifMarker(canvas, startGif, startNode.x, startNode.y);
                }


                imageView.post(() -> imageView.setImageBitmap(bitmap));

                if (index < path.size()) {
                    handler.postDelayed(this, 2);  // 2ms 간격으로 계속 그림
                }
            }
        };

        handler.post(drawRunnable);
    }

    // GIF 이미지를 중심 위치에 맞춰 그리는 공통 메서드
    static void drawGifMarker(Canvas canvas, Bitmap gif, float x, float y) {
        int radius = 10;
        Bitmap resizedGif = Bitmap.createScaledBitmap(gif, radius * 15, radius * 15, false);
        canvas.drawBitmap(resizedGif, x - radius * 7.5f, y - radius * 13.5f, null);
    }

    // 일반 점 그리기
    public static void drawPoint(Bitmap bitmap, int x, int y, int color, int radius) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(x, y, radius, paint);
    }
}
