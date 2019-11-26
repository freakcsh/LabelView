package com.freak.label.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.freak.label.R;
import com.freak.label.utils.ScreenUtils;

public class MaxHeightScrollView extends ScrollView {
    private int maxScroll = 220;
    private int minScroll = 75;

    public MaxHeightScrollView(Context context) {
        this(context, null);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.MaxHeightScrollView_maxScrollHeight) {
                maxScroll = typedArray.getDimensionPixelOffset(attr, maxScroll);
            } else if (attr == R.styleable.MaxHeightScrollView_minScrollHeight) {
                minScroll = typedArray.getDimensionPixelOffset(attr, minScroll);
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int chileHeight = child.getMeasuredHeight();
            if (chileHeight > height) {
                height = chileHeight;
            }
            float heightDp = ScreenUtils.px2Dp(getContext(), height);
            if (heightDp > maxScroll) {
                int maxHeight = ScreenUtils.dp2PxInt(getContext(), maxScroll);
                setMeasuredDimension(width, maxHeight);
            } else if (heightDp < minScroll) {
                int minHeight = ScreenUtils.dp2PxInt(getContext(), heightDp);
                setMeasuredDimension(width, minHeight);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }
}
