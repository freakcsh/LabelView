package com.freak.label;

import android.util.Log;
import android.view.View;

import java.util.HashSet;
import java.util.List;


public abstract class LabelAdapter<T> {
    private List<T> labelData;
    private OnDataChangeListener onDataChangeListener;
    @Deprecated
    private HashSet<Integer> mCheckedPosList = new HashSet<Integer>();

    public LabelAdapter(List<T> labelData) {
        this.labelData = labelData;
    }

    public void setLabelData(List<T> labelData) {
        this.labelData = labelData;
    }

    public List<T> getLabelData() {
        return labelData;
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
    }

    public int getCount() {
        return labelData == null ? 0 : labelData.size();
    }

    public void notifyDataChange() {
        if (onDataChangeListener != null) {
            onDataChangeListener.onChange();
        }
    }

    @Deprecated
    HashSet<Integer> getPreCheckedList() {
        return mCheckedPosList;
    }

    public T getItem(int position) {
        return labelData.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);

    public void onSelected(int position, View view) {
        Log.d("freak", "onSelected " + position);
    }

    public void unSelected(int position, View view) {
        Log.d("freak", "onSelected " + position);
    }

    public boolean setSelected(int position, T t) {
        return false;
    }

     interface OnDataChangeListener {
        void onChange();
    }
}
