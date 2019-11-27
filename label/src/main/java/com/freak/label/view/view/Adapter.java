package com.freak.label.view.view;

import java.util.List;

public class Adapter<T> {
    private List<T> data;

    interface OnDataChangeListener {
        void onDataChange();
    }
}
