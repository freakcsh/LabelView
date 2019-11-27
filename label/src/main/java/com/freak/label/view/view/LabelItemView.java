package com.freak.label.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.freak.label.R;
import com.freak.label.utils.ScreenUtils;


public class LabelItemView extends AppCompatTextView {
    private SkuAttribute attributeValue;

    public LabelItemView(Context context) {
        super(context);
        init(context);
    }

    public LabelItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LabelItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundResource(R.drawable.sku_item_selector);
        setTextColor(getResources().getColorStateList(R.color.sku_item_text_selector));
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        setSingleLine();
        setGravity(Gravity.CENTER);
        setPadding(ScreenUtils.dp2PxInt(context, 10), 0, ScreenUtils.dp2PxInt(context, 10), 0);

        setMinWidth(ScreenUtils.dp2PxInt(context, 45));
        setMaxWidth(ScreenUtils.dp2PxInt(context, 200));
    }

    public SkuAttribute  getAttributeValue() {
        return attributeValue;
    }

    /**
     * 设置属性的val_id
     *
     * @param attributeValue
     */
    public void setAttributeValue(SkuAttribute attributeValue) {
        this.attributeValue = attributeValue;
        setText(attributeValue.getAttr_name());
    }
}
