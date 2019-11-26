package com.freak.label.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.freak.label.R;
import com.freak.label.widget.FlowLayout;
import com.freak.label.widget.MaxHeightScrollView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LabelFlowLayout1 extends MaxHeightScrollView implements LabelAdapter.OnDataChangeListener {
    private LabelAdapter labelAdapter;
    private int selectedMax = -1;//-1为不限制数量
    private static final String TAG = "LabelFlowLayout";
    private Set<Integer> mSelectedView = new HashSet<>();
    private OnSelectedListener onSelectedListener;
    private OnLabelClickListener onLabelClickListener;
    private FlowLayout attributeValueLayout;

    interface OnLabelClickListener {
        boolean onLabelClick(View view, int position, ViewGroup parent);
    }

    interface OnSelectedListener {
        void onSelected(Set<Integer> selectPosSet);
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public void setOnLabelClickListener(OnLabelClickListener onLabelClickListener) {
        this.onLabelClickListener = onLabelClickListener;
    }

    public LabelFlowLayout1(Context context) {
        this(context, null);
    }

    public LabelFlowLayout1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelFlowLayout1(Context context, AttributeSet attrs, int defStyleAttr) {
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
        attributeValueLayout = new FlowLayout(context);
        attributeValueLayout.setId(View.generateViewId());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LabelItemView labelView = (LabelItemView) getChildAt(i);
            if (labelView.getVisibility() == View.GONE) {
                continue;
            }
            if (labelView.getLabelView().getVisibility() == View.GONE) {
                labelView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        LabelItemView labelViewContainer = null;
        HashSet preCheckedList = labelAdapter.getPreCheckedList();
        for (int i = 0; i < adapter.getCount(); i++) {
            View labelView = adapter.getView(this, i, adapter.getItem(i));
            labelViewContainer = new LabelItemView(getContext());
            labelView.setDuplicateParentStateEnabled(true);
            //设置间距
            if (labelView.getLayoutParams() != null) {
                labelViewContainer.setLayoutParams(labelView.getLayoutParams());
            } else {
                MarginLayoutParams marginLayoutParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                marginLayoutParams.setMargins(dip2px(
                        getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5)
                );
                labelViewContainer.setLayoutParams(marginLayoutParams);
            }
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            labelView.setLayoutParams(layoutParams);
            labelViewContainer.addView(labelView);
            attributeValueLayout.addView(labelViewContainer);
            addView(attributeValueLayout);

            if (preCheckedList.contains(i)) {
                setChildChecked(i, labelViewContainer);
            }
            if (labelAdapter.setSelected(i, adapter.getItem(i))) {
                setChildChecked(i, labelViewContainer);
            }
            labelView.setClickable(false);
            final LabelItemView finalLabelViewContainer = labelViewContainer;
            final int position = i;
            labelViewContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSelect(finalLabelViewContainer, position);
                    if (onLabelClickListener != null) {
                        onLabelClickListener.onLabelClick(finalLabelViewContainer, position, LabelFlowLayout1.this);
                    }
                }
            });
            mSelectedView.addAll(preCheckedList);
        }
    }

    private void doSelect(LabelItemView child, int position) {
        if (!child.isChecked()) {
            if (selectedMax == 1 && mSelectedView.size() == 1) {
                Iterator<Integer> iterator = mSelectedView.iterator();
                Integer integer = iterator.next();
                LabelItemView labelView = (LabelItemView) getChildAt(integer);
                setChildUnChecked(integer, labelView);
                setChildChecked(position, child);

                mSelectedView.remove(integer);
                mSelectedView.add(position);
            } else {
                if (selectedMax > 0 && mSelectedView.size() >= selectedMax) {
                    return;
                }
                setChildChecked(position, child);
                mSelectedView.add(position);
            }
        } else {
            setChildUnChecked(position, child);
            mSelectedView.remove(position);
        }
        if (onSelectedListener != null) {
            onSelectedListener.onSelected(new HashSet<Integer>(mSelectedView));
        }
    }

    private void setChildUnChecked(int position, LabelItemView view) {
        view.setChecked(false);
        labelAdapter.unSelected(position, view.getLabelView());
    }

    private void setChildChecked(int position, LabelItemView view) {
        view.setChecked(true);
        labelAdapter.onSelected(position, view.getLabelView());
    }

    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_DEFAULT = "key_default";

    public void setSelectedMax(int selectedMax) {
        if (mSelectedView.size() > selectedMax) {
            Log.w(TAG, "you has already select more than " + selectedMax + " views , so it will be clear .");
            mSelectedView.clear();
        }
        this.selectedMax = selectedMax;
    }

    public Set<Integer> getSelectedList() {
        return new HashSet<Integer>(mSelectedView);
    }

    public LabelAdapter getLabelAdapter() {
        return labelAdapter;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());
        String selectPos = "";
        if (mSelectedView.size() > 0) {
            for (int key : mSelectedView) {
                selectPos += key + "|";
            }
            selectPos = selectPos.substring(0, selectPos.length() - 1);
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String mSelectPos = bundle.getString(KEY_CHOOSE_POS);
            if (!TextUtils.isEmpty(mSelectPos)) {
                String[] split = mSelectPos.split("\\|");
                for (String pos : split) {
                    int index = Integer.parseInt(pos);
                    mSelectedView.add(index);

                    LabelItemView labelView = (LabelItemView) getChildAt(index);
                    if (labelView != null) {
                        setChildChecked(index, labelView);
                    }
                }
            }
            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onChange() {
        mSelectedView.clear();
        changeAdapter();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
