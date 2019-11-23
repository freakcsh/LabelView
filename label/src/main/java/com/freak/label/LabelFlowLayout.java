package com.freak.label;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

public class LabelFlowLayout extends FlowLayout implements LabelAdapter.OnDataChangeListener {
    private LabelAdapter labelAdapter;
    private int selectedMax = -1;//-1为不限制数量
    private static final String TAG = "LabelFlowLayout";
    private Set<Integer> mSelectedView = new HashSet<>();
    private OnSelectedListener onSelectedListener;
    private OnLabelClickListener onLabelClickListener;

    interface OnLabelClickListener {
        boolean onLabelClick(View view, int position, FlowLayout parent);
    }

    interface OnSelectedListener {
        void onSeleted(Set<Integer> selectPosSet);
    }

    public LabelFlowLayout(Context context) {
        this(context, null);
    }

    public LabelFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs);
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelFlowLayout);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.LabelFlowLayout_max_select) {
                selectedMax = typedArray.getInteger(attr, selectedMax);
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LabelView labelView = (LabelView) getChildAt(i);
            if (labelView.getVisibility() == View.GONE) {
                continue;
            }
            if (labelView.getLabelView().getVisibility() == View.GONE) {
                labelView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public OnSelectedListener getOnSelectedListener() {
        return onSelectedListener;
    }

    public OnLabelClickListener getOnLabelClickListener() {
        return onLabelClickListener;
    }

    public void setAdapter(LabelAdapter adapter) {
        labelAdapter = adapter;
        labelAdapter.setOnDataChangeListener(this);
        mSelectedView.clear();
        changeAdapter();
    }

    public void changeAdapter() {
        removeAllViews();
        LabelAdapter adapter = labelAdapter;
        LabelView labelView = null;
    }

    @Override
    public void onChange() {

    }
}
