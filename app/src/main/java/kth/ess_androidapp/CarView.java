package kth.ess_androidapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CarView extends View {

    private char[] distances = {0x10, 0xA7, 0xFF};

    public CarView(Context context) {
        super(context);
        init(null, 0);
    }

    public CarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CarView, defStyle, 0);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int carPeekSize = 20;
        int arcBaseSeparation = (3*getWidth()/4)/3;
        int arcCountPerBase = 10;
        int arcSeparation = (getHeight()/4)/arcCountPerBase;

        Paint paint = new Paint();
        paint.setARGB(150, 200, 20, 150);
        Path path = new Path();
        for(int i = -1; i <= 1; ++i){
            @SuppressLint("DrawAllocation") RectF ovalOrigin = new RectF(getWidth()/2 + (i*arcBaseSeparation) - arcSeparation/2,
                                            getHeight() - arcSeparation - carPeekSize,
                                            getWidth()/2 + (i*arcBaseSeparation) + arcSeparation/2,
                                            getHeight() - arcSeparation/2 - carPeekSize);
            canvas.drawOval(ovalOrigin, paint);
            for(int j = 1; j <= arcCountPerBase; ++j){
                float jPercentage = ((float)j)/arcCountPerBase;
                int depth = (int)(jPercentage*getHeight()*0.4f*(1.0f-jPercentage/3));
                int width = (int)(jPercentage*(getWidth()/3)*0.8f);
                int thickness = 8;
                Log.d("CarView", "depth:"+depth);
                Log.d("CarView", "width:"+width);
                RectF ovalOut = new RectF(ovalOrigin.left-width, ovalOrigin.top-depth,
                                        ovalOrigin.right+width, ovalOrigin.bottom);
                RectF ovalIn = new RectF(ovalOut.left, ovalOut.top-thickness,
                                        ovalOut.right, ovalOut.bottom);
                path.reset();
                path.arcTo(ovalOut, 225f, 90.0f);
                path.arcTo(ovalIn, 225f + 90.0f, -90.0f);
                path.close();

                if(distances[i+1] >= 0xFF*(j+1)/(arcCountPerBase+2)){
                    paint.setARGB(150,80,70,80);
                }else{
                    paint.setARGB(150, 200, 20, 150);
                }

                canvas.drawPath(path, paint);
            }
        }

        // Draw the text.
        /*canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
}
