package com.example.navermapsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

public class CustomGridLayout extends GridLayout {

    private Paint paint;

    public CustomGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomGridLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK); // 선 색상 설정
        paint.setStrokeWidth(2);     // 선 굵기 설정
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        int childCount = getChildCount();
        int columnCount = getColumnCount(); // GridLayout의 열 개수
        int layoutWidth = getWidth();      // 전체 GridLayout 폭

        // 세로선 그리기
        for (int col = 1; col < columnCount; col++) { // 첫 번째 열 제외
            float columnX = getChildAt(col).getLeft(); // 현재 열의 왼쪽 위치
            canvas.drawLine(columnX, 0, columnX, getHeight(), paint); // 화면 끝까지 세로선
        }

        // 숫자 위에만 가로선 그리기
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            // 숫자인 TextView만 처리
            if (child instanceof TextView) {
                CharSequence text = ((TextView) child).getText();
                if (text != null && text.toString().matches("\\d+")) { // 숫자인지 확인
                    float rowY = child.getTop(); // 현재 숫자의 상단 위치
                    canvas.drawLine(0, rowY, layoutWidth, rowY, paint); // 화면 끝까지 가로선
                }
            }
        }
    }
}
