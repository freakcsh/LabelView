package com.freak.label.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.freak.label.widget.FlowLayout;
import com.freak.label.widget.MaxHeightScrollView;

import java.util.Set;

public class LabelView extends MaxHeightScrollView implements LabelFlowLayout.OnLabelClickListener , LabelFlowLayout.OnSelectedListener {
    public LabelView(Context context) {
        this(context,null);
    }

    public LabelView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onLabelClick(View view, int position, FlowLayout parent) {
        return false;
    }

    @Override
    public void onSelected(Set<Integer> selectPosSet) {

    }
}
