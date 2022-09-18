package com.portfolio.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameBoard extends View {

    Paint line;

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        line = new Paint();
        line.setStyle(Paint.Style.STROKE);
        line.setColor(Color.BLACK);
        line.setStrokeWidth(25.0F);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        setMeasuredDimension(Math.min(w, h), Math.min(w, h));
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width_height = Math.min(getWidth(), getHeight());
        float thirdOfScreen = width_height/3;

        //rows
        canvas.drawLine(0, thirdOfScreen, width_height, thirdOfScreen, line);
        canvas.drawLine(0, thirdOfScreen*2, width_height, thirdOfScreen*2, line);

        //columns
        canvas.drawLine(thirdOfScreen, 0, thirdOfScreen, width_height, line);
        canvas.drawLine(thirdOfScreen*2, 0, thirdOfScreen*2, width_height, line);

    }
}
