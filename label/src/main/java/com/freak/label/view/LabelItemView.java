package com.freak.label.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class LabelItemView extends FrameLayout implements Checkable {
    private boolean isCheck;
    private static final int[] CHECK_STATE = new int[]{android.R.attr.state_checked};

    public View getLabelView() {
        return getChildAt(0);
    }

    public LabelItemView(Context context) {
        super(context);
    }

    public LabelItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LabelItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace);
        if (isChecked()) {
            mergeDrawableStates(states, CHECK_STATE);
        }
        return states;
    }

    @Override
    public void setChecked(boolean checked) {
        if (this.isCheck != checked) {
            this.isCheck = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return isCheck;
    }

    @Override
    public void toggle() {
        setChecked(!isCheck);
    }
}
