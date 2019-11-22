package com.freak.labelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleView extends View {
    private int radius;
    private int hideHeight;
    private int hideWidth;
    private int marginCircleLeftPoint;
    private Path path;
    private Path path1;
    private Paint paint;
    private int width;
    private int height;
    private RectF rectF;
    private int startColor = Color.WHITE;
    private int endColor = Color.WHITE;
    private int orientation = BOTTOM;
    private static final int TOP = 0;
    private static final int BOTTOM = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;


    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        orientation = BOTTOM;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CircleView_CircleViewStartColor) {
                startColor = typedArray.getColor(attr, startColor);
            } else if (attr == R.styleable.CircleView_CircleViewEndColor) {
                endColor = typedArray.getColor(attr, endColor);
            } else if (attr == R.styleable.CircleView_CircleViewRadius) {
                radius = typedArray.getDimensionPixelOffset(attr, radius);
            } else if (attr == R.styleable.CircleView_CircleViewMarginCirclePoint) {
                marginCircleLeftPoint = typedArray.getDimensionPixelOffset(attr, marginCircleLeftPoint);
            } else if (attr == R.styleable.CircleView_CircleViewRadiusHideOrientation) {
                orientation = typedArray.getInteger(attr, orientation);
            }
        }
        typedArray.recycle();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setShader(new LinearGradient(0, 0, 2 * radius, 2 * radius, new int[]{startColor, endColor}, null, Shader.TileMode.MIRROR));
        path = new Path();
        path1 = new Path();
        switch (orientation) {
            case TOP:
                rectF = new RectF(0, 0, 2 * radius, marginCircleLeftPoint);
                break;
            case BOTTOM:
                rectF = new RectF(0, 2 * radius - marginCircleLeftPoint, 2 * radius, 2 * radius);
                break;
            case LEFT:
                rectF = new RectF(0, 0, marginCircleLeftPoint, 2 * radius);
                break;
            case RIGHT:
                rectF = new RectF(2 * radius - marginCircleLeftPoint, 0, 2 * radius, 2 * radius);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //reset()一定要调用，否则息屏之后进行刷新是逻辑判断会显示不了UI
        path.reset();
        path1.reset();
        path.addCircle(width / 2, height / 2, radius, Path.Direction.CW);
        path1.addRect(rectF, Path.Direction.CW);
        path.op(path1, Path.Op.DIFFERENCE);
        canvas.drawPath(path, paint);
    }
}
