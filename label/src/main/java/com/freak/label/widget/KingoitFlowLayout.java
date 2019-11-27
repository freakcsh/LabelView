package com.freak.label.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freak.label.R;

import java.util.ArrayList;
import java.util.List;

/**
 * kingoit,流式布局
 * 20180815-修复不可滑动问题
 * @author zuo
 * @date 2018/7/16 11:48
 */
public class KingoitFlowLayout extends ViewGroup {
    //记录每个View的位置
    private List<ChildPos> mChildPos = new ArrayList<ChildPos>();
    private float textSize;
    private int textColor;
    private int textColorSelector;
    private float shapeRadius;
    private int shapeLineColor;
    private int shapeBackgroundColor;
    private int shapeBackgroundColorSelector;
    private float shapeLineWidth;
    private int deleteBtnColor;
    /**
     * 是否是可删除模式
     */
    private boolean isDeleteMode;
    /**
     * 记录所有选中着的词
     */
    private List<String> mAllSelectedWords = new ArrayList<>();

    private class ChildPos {
        int left, top, right, bottom;

        public ChildPos(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    public KingoitFlowLayout(Context context) {
        this(context, null);
    }

    public KingoitFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initAttributes(context, attrs);
    }

    /**
     * 最终调用这个构造方法
     *
     * @param context  上下文
     * @param attrs    xml属性集合
     * @param defStyle Theme中定义的style
     */
    public KingoitFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 流式布局属性设置
     *
     * @param context
     * @param attrs
     */
    @SuppressLint("ResourceAsColor")
    private void initAttributes(Context context, AttributeSet attrs) {
        @SuppressLint("Recycle")
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KingoitFlowLayout);
        textSize = typedArray.getDimension(R.styleable.KingoitFlowLayout_flowLayoutTextSize, 16);
        textColor = typedArray.getColor(R.styleable.KingoitFlowLayout_flowLayoutTextColor, Color.parseColor("#FF4081"));
        textColorSelector = typedArray.getResourceId(R.styleable.KingoitFlowLayout_flowLayoutTextColorSelector, 0);
        shapeRadius = typedArray.getDimension(R.styleable.KingoitFlowLayout_flowLayoutRadius, 40f);
        shapeLineColor = typedArray.getColor(R.styleable.KingoitFlowLayout_flowLayoutLineColor, Color.parseColor("#ADADAD"));
        shapeBackgroundColor = typedArray.getColor(R.styleable.KingoitFlowLayout_flowLayoutBackgroundColor, Color.parseColor("#c5cae9"));
        shapeBackgroundColorSelector = typedArray.getResourceId(R.styleable.KingoitFlowLayout_flowLayoutBackgroundColorSelector, 0);
        shapeLineWidth = typedArray.getDimension(R.styleable.KingoitFlowLayout_flowLayoutLineWidth, 4f);
        deleteBtnColor = typedArray.getColor(R.styleable.KingoitFlowLayout_flowLayoutDeleteBtnColor, Color.GRAY);
    }

    /**
     * 测量宽度和高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取流式布局的宽度和模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //获取流式布局的高度和模式
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //使用wrap_content的流式布局的最终宽度和高度
        int width = 0, height = 0;
        //记录每一行的宽度和高度
        int lineWidth = 0, lineHeight = 0;
        //得到内部元素的个数
        int count = getChildCount();
        mChildPos.clear();
        for (int i = 0; i < count; i++) {
            //获取对应索引的view
            View child = getChildAt(i);
            //测量子view的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //子view占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //子view占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //换行
            if (lineWidth + childWidth > widthSize - getPaddingLeft() - getPaddingRight()) {
                //取最大的行宽为流式布局宽度
                width = Math.max(width, lineWidth);
                //叠加行高得到流式布局高度
                height += lineHeight;
                //重置行宽度为第一个View的宽度
                lineWidth = childWidth;
                //重置行高度为第一个View的高度
                lineHeight = childHeight;
                //记录位置
                mChildPos.add(new ChildPos(
                        getPaddingLeft() + lp.leftMargin,
                        getPaddingTop() + height + lp.topMargin,
                        getPaddingLeft() + childWidth - lp.rightMargin,
                        getPaddingTop() + height + childHeight - lp.bottomMargin));
            } else {  //不换行
                //记录位置
                mChildPos.add(new ChildPos(
                        getPaddingLeft() + lineWidth + lp.leftMargin,
                        getPaddingTop() + height + lp.topMargin,
                        getPaddingLeft() + lineWidth + childWidth - lp.rightMargin,
                        getPaddingTop() + height + childHeight - lp.bottomMargin));
                //叠加子View宽度得到新行宽度
                lineWidth += childWidth;
                //取当前行子View最大高度作为行高度
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //最后一个控件
            if (i == count - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }
        // 得到最终的宽高
        // 宽度：如果是AT_MOST模式，则使用我们计算得到的宽度值，否则遵循测量值
        // 高度：只要布局中内容的高度大于测量高度，就使用内容高度（无视测量模式）；否则才使用测量高度
        int flowLayoutWidth = widthMode == MeasureSpec.AT_MOST ? width + getPaddingLeft() + getPaddingRight() : widthSize;
        int flowLayoutHeight = heightMode == MeasureSpec.AT_MOST ? height + getPaddingTop() + getPaddingBottom() : heightSize;
        //真实高度
        realHeight = height + getPaddingTop() + getPaddingBottom();
        //测量高度
        measuredHeight = heightSize;
        if (heightMode == MeasureSpec.EXACTLY) {
            realHeight = Math.max(measuredHeight, realHeight);
        }
        scrollable = realHeight > measuredHeight;
        // 设置最终的宽高
        setMeasuredDimension(flowLayoutWidth, flowLayoutHeight);
    }

    /**
     * 让ViewGroup能够支持margin属性
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 设置每个View的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ChildPos pos = mChildPos.get(i);
            //设置View的左边、上边、右边底边位置
            child.layout(pos.left, pos.top, pos.right, pos.bottom);
        }
    }

    public void addItemView(LayoutInflater inflater, String tvName) {
        //加载 ItemView并设置名称，并设置名称
        View view = inflater.inflate(R.layout.kingoit_flow_layout, this, false);
        ImageView delete = view.findViewById(R.id.delete);
        if (isDeleteMode) {
            delete.setVisibility(VISIBLE);
        } else {
            delete.setVisibility(GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            delete.setImageTintList(ColorStateList.valueOf(deleteBtnColor));
        }
        TextView textView = view.findViewById(R.id.value);
        textView.setTextSize(textSize / getContext().getResources().getDisplayMetrics().scaledDensity);
        if (textColorSelector != 0) {
            ColorStateList csl = getResources().getColorStateList(textColorSelector);
            textView.setTextColor(csl);
        } else {
            textView.setTextColor(textColor);
        }
        textView.setPadding(20, 4, 20, 4);
        textView.setText(tvName);
        //动态设置shape
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(shapeRadius);
        drawable.setStroke((int) shapeLineWidth, shapeLineColor);
        if (shapeBackgroundColorSelector != 0) {
            ColorStateList csl = getResources().getColorStateList(shapeBackgroundColorSelector);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable.setColor(csl);
            }
        } else {
            drawable.setColor(shapeBackgroundColor);
        }
        textView.setBackgroundDrawable(drawable);

        //把 ItemView加入流式布局
        this.addView(view);
    }


    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    public void setDeleteMode(boolean deleteMode) {
        isDeleteMode = deleteMode;
    }

    //---20180815---修复不可滑动bug----start----
    private boolean scrollable; // 是否可以滚动
    private int measuredHeight; // 测量得到的高度
    private int realHeight; // 整个流式布局控件的实际高度
    private int scrolledHeight = 0; // 已经滚动过的高度
    private int startY; // 本次滑动开始的Y坐标位置
    private int offsetY; // 本次滑动的偏移量
    private boolean pointerDown; // 在ACTION_MOVE中，视第一次触发为手指按下，从第二次触发开始计入正式滑动

    /**
     * 滚动事件的处理，当布局可以滚动（内容高度大于测量高度）时，对手势操作进行处理
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 只有当布局可以滚动的时候（内容高度大于测量高度的时候），且处于拦截模式，才会对手势操作进行处理
        if (scrollable && isInterceptedTouch) {
            int currY = (int) event.getY();
            switch (event.getAction()) {
                // 因为ACTION_DOWN手势可能是为了点击布局中的某个子元素，因此在onInterceptTouchEvent()方法中没有拦截这个手势
                // 因此，在这个事件中不能获取到startY，也因此才将startY的获取移动到第一次滚动的时候进行
                case MotionEvent.ACTION_DOWN:
                    break;
                // 当第一次触发ACTION_MOVE事件时，视为手指按下；以后的ACTION_MOVE事件才视为滚动事件
                case MotionEvent.ACTION_MOVE:
                    // 用pointerDown标志位只是手指是否已经按下
                    if (!pointerDown) {
                        startY = currY;
                        pointerDown = true;
                    } else {
                        offsetY = startY - currY; // 下滑大于0
                        // 布局中的内容跟随手指的滚动而滚动
                        // 用scrolledHeight记录以前的滚动事件中滚动过的高度（因为不一定每一次滚动都是从布局的最顶端开始的）
                        this.scrollTo(0, scrolledHeight + offsetY);
                    }
                    break;
                // 手指抬起时，更新scrolledHeight的值；
                // 如果滚动过界（滚动到高于布局最顶端或低于布局最低端的时候），设置滚动回到布局的边界处
                case MotionEvent.ACTION_UP:
                    scrolledHeight += offsetY;
                    if (scrolledHeight + offsetY < 0) {
                        this.scrollTo(0, 0);
                        scrolledHeight = 0;
                    } else if (scrolledHeight + offsetY + measuredHeight > realHeight) {
                        this.scrollTo(0, realHeight - measuredHeight);
                        scrolledHeight = realHeight - measuredHeight;
                    }
                    // 手指抬起后别忘了重置这个标志位
                    pointerDown = false;
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 事件拦截，当手指按下或抬起的时候不进行拦截（因为可能这个操作只是点击了布局中的某个子元素）；
     * 当手指移动的时候，才将事件拦截；
     * 因增加最小滑动距离防止点击时误触滑动
     */
    private boolean isInterceptedTouch;
    private int startYY = 0;
    private boolean pointerDownY;
    private int minDistance = 10;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int currY = (int) ev.getY();
        int offsetY = 0;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointerDownY = true;
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerDownY) {
                    startYY = currY;
                } else {
                    offsetY = currY - startYY;
                }
                pointerDownY = false;
                intercepted = Math.abs(offsetY) > minDistance;
                break;
            case MotionEvent.ACTION_UP:
                // 手指抬起后别忘了重置这个标志位
                intercepted = false;
                break;
            default:
                break;
        }
        isInterceptedTouch = intercepted;
        return intercepted;
    }

    //---20180815---修复不可滑动bug----end----

    /**
     * 流式布局显示
     * Toast.makeText(FlowLayoutActivity.this, keywords, Toast.LENGTH_SHORT).show();
     *
     * @param list
     */
    public void showTag(final List<String> list, final ItemClickListener listener) {
        removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            final String keywords = list.get(i);
            addItemView(LayoutInflater.from(getContext()), keywords);
            final int finalI = i;
            getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDeleteMode()) {
                        list.remove(keywords);
                        showTag(list, listener);
                    } else {
                        View child = getChildAt(finalI);
                        child.setSelected(!child.isSelected());
                        if (child.isSelected()) {
                            mAllSelectedWords.add(list.get(finalI));
                        } else {
                            mAllSelectedWords.remove(list.get(finalI));
                        }
                        listener.onClick(keywords, mAllSelectedWords);
                    }
                }
            });
        }
    }

    public interface ItemClickListener {
        /**
         * item 点击事件
         *
         * @param currentSelectedkeywords
         * @param allSelectedKeywords
         */
        void onClick(String currentSelectedkeywords, List<String> allSelectedKeywords);
    }
}