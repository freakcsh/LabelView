package com.freak.label.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.freak.label.R;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private static final String LOG_TAG = FlowLayout.class.getSimpleName();

    /**
     * 子视图间距的特殊值。
     * SPACING_AUTO 表示实际间距是根据容器的大小和子视图的数量计算的，因此子视图均匀地放置在容器中。
     */
    public static final int SPACING_AUTO = -65536;

    /**
     * 最后一行中子视图的水平间距的特殊值SPACING_ALIGN表示最后一行中子视图的水平间距与上一行中使用的间距保持相同。
     * 如果只有一行，则将忽略此值，并将根据childSpacing计算间距。
     */
    public static final int SPACING_ALIGN = -65537;

    private static final int SPACING_UNDEFINED = -65538;

    private static final boolean DEFAULT_FLOW = true;
    private static final int DEFAULT_CHILD_SPACING = 0;
    private static final int DEFAULT_CHILD_SPACING_FOR_LAST_ROW = SPACING_UNDEFINED;
    private static final float DEFAULT_ROW_SPACING = 0;
    private static final boolean DEFAULT_RTL = false;
    private static final int DEFAULT_MAX_ROWS = Integer.MAX_VALUE;
    /**
     * 设置当没有足够的空间时是否允许子视图流到下一行。
     * true 允许流动。false将所有子视图限制在一行中
     */
    private boolean mFlow = DEFAULT_FLOW;
    /**
     * 子视图之间的水平间距。
     */
    private int mChildSpacing = DEFAULT_CHILD_SPACING;
    /**
     * 最后一行的子视图之间的水平间距。
     */
    private int mChildSpacingForLastRow = DEFAULT_CHILD_SPACING_FOR_LAST_ROW;
    /**
     * 行之间的垂直间距。
     */
    private float mRowSpacing = DEFAULT_ROW_SPACING;
    private float mAdjustedRowSpacing = DEFAULT_ROW_SPACING;
    /**
     * 是否从右边开始，默认false false：左边 true：右边
     */
    private boolean startRight = DEFAULT_RTL;
    /**
     * FlowLayout的最大行数
     */
    private int mMaxRows = DEFAULT_MAX_ROWS;
    /**
     * 水平行距list
     */
    private List<Float> mHorizontalSpacingForRow = new ArrayList<>();
    /**
     * 行高list
     */
    private List<Integer> mHeightForRow = new ArrayList<>();
    /**
     * 每行的子 view num
     */
    private List<Integer> mChildNumForRow = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        try {
            mFlow = typedArray.getBoolean(R.styleable.FlowLayout_flow, DEFAULT_FLOW);
            try {
                mChildSpacing = typedArray.getInt(R.styleable.FlowLayout_childSpacing, DEFAULT_CHILD_SPACING);
            } catch (NumberFormatException e) {
                mChildSpacing = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_childSpacing, (int) dpToPx(DEFAULT_CHILD_SPACING));
            }
            try {
                mChildSpacingForLastRow = typedArray.getInt(R.styleable.FlowLayout_childSpacingForLastRow, SPACING_UNDEFINED);
            } catch (NumberFormatException e) {
                mChildSpacingForLastRow = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_childSpacingForLastRow, (int) dpToPx(DEFAULT_CHILD_SPACING));
            }
            try {
                mRowSpacing = typedArray.getInt(R.styleable.FlowLayout_rowSpacing, 0);
            } catch (NumberFormatException e) {
                mRowSpacing = typedArray.getDimension(R.styleable.FlowLayout_rowSpacing, dpToPx(DEFAULT_ROW_SPACING));
            }
            mMaxRows = typedArray.getInt(R.styleable.FlowLayout_maxRows, DEFAULT_MAX_ROWS);
            startRight = typedArray.getBoolean(R.styleable.FlowLayout_startRight, DEFAULT_RTL);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * Mode：
         * MeasureSpec.UNSPECIFIED：度量指定模式：父view没有对子view施加任何约束。 它可以是任何大小。
         * MeasureSpec.EXACTLY：度量指定模式：父view已确定子view的确切大小。 不管子view想要多大，都会给子view以这些界限。
         * MeasureSpec.AT_MOST：度量指定模式：子view可以根据需要的大小而定，最大可以达到指定的大小。
         */
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mHorizontalSpacingForRow.clear();
        mChildNumForRow.clear();
        mHeightForRow.clear();

        int measuredHeight = 0, measuredWidth = 0, childCount = getChildCount();
        int rowWidth = 0, maxChildHeightInRow = 0, childNumInRow = 0;
        int rowSize = widthSize - getPaddingLeft() - getPaddingRight();
        //是否允许自动换行
        boolean allowFlow = widthMode != MeasureSpec.UNSPECIFIED && mFlow;
        int childSpacing = mChildSpacing == SPACING_AUTO && widthMode == MeasureSpec.UNSPECIFIED ? 0 : mChildSpacing;
        float tmpSpacing = childSpacing == SPACING_AUTO ? 0 : childSpacing;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams childParams = child.getLayoutParams();
            int horizontalMargin = 0, verticalMargin = 0;
            if (childParams instanceof MarginLayoutParams) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, measuredHeight);
                MarginLayoutParams marginParams = (MarginLayoutParams) childParams;
                horizontalMargin = marginParams.leftMargin + marginParams.rightMargin;
                verticalMargin = marginParams.topMargin + marginParams.bottomMargin;
            } else {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }

            int childWidth = child.getMeasuredWidth() + horizontalMargin;
            int childHeight = child.getMeasuredHeight() + verticalMargin;
            if (allowFlow && rowWidth + childWidth > rowSize) { // 需要换行
                // 保存当前行的参数
                mHorizontalSpacingForRow.add(getSpacingForRow(childSpacing, rowSize, rowWidth, childNumInRow));
                mChildNumForRow.add(childNumInRow);
                mHeightForRow.add(maxChildHeightInRow);
                if (mHorizontalSpacingForRow.size() <= mMaxRows) {
                    measuredHeight += maxChildHeightInRow;
                }
                measuredWidth = Math.max(measuredWidth, rowWidth);

                // 将子视图放置到下一行
                childNumInRow = 1;
                rowWidth = childWidth + (int) tmpSpacing;
                maxChildHeightInRow = childHeight;
            } else {
                childNumInRow++;
                rowWidth += childWidth + tmpSpacing;
                maxChildHeightInRow = Math.max(maxChildHeightInRow, childHeight);
            }
        }

        // 测量最后一行中剩余的子视图
        if (mChildSpacingForLastRow == SPACING_ALIGN) {
            // 对于SPACING_ALIGN，如果有多个行，则使用与上面行相同的间距。
            if (mHorizontalSpacingForRow.size() >= 1) {
                mHorizontalSpacingForRow.add(mHorizontalSpacingForRow.get(mHorizontalSpacingForRow.size() - 1));
            } else {
                mHorizontalSpacingForRow.add(getSpacingForRow(childSpacing, rowSize, rowWidth, childNumInRow));
            }
        }
//        else if (mChildSpacingForLastRow == SPACING_AUTO) {
//            // 对于SPACING_AUTO和特定的DP值，将它们应用于间距策略。
//            mHorizontalSpacingForRow.add(getSpacingForRow(mChildSpacingForLastRow, rowSize, rowWidth, childNumInRow));
//        }
        else {
            // 对于SPACING_UNDEFINED，将child间距应用于最后一行的间距策略
            mHorizontalSpacingForRow.add(getSpacingForRow(childSpacing, rowSize, rowWidth, childNumInRow));
        }

        mChildNumForRow.add(childNumInRow);
        mHeightForRow.add(maxChildHeightInRow);
        if (mHorizontalSpacingForRow.size() <= mMaxRows) {
            measuredHeight += maxChildHeightInRow;
        }
        measuredWidth = Math.max(measuredWidth, rowWidth);

        if (childSpacing == SPACING_AUTO) {
            measuredWidth = widthSize;
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            measuredWidth = measuredWidth + getPaddingLeft() + getPaddingRight();
        } else {
            measuredWidth = Math.min(measuredWidth + getPaddingLeft() + getPaddingRight(), widthSize);
        }

        measuredHeight += getPaddingTop() + getPaddingBottom();
        int rowNum = Math.min(mHorizontalSpacingForRow.size(), mMaxRows);
        float rowSpacing = mRowSpacing == SPACING_AUTO && heightMode == MeasureSpec.UNSPECIFIED
                ? 0 : mRowSpacing;
        if (rowSpacing == SPACING_AUTO) {
            if (rowNum > 1) {
                mAdjustedRowSpacing = (heightSize - measuredHeight) / (rowNum - 1);
            } else {
                mAdjustedRowSpacing = 0;
            }
            measuredHeight = heightSize;
        } else {
            mAdjustedRowSpacing = rowSpacing;
            if (rowNum > 1) {
                measuredHeight = heightMode == MeasureSpec.UNSPECIFIED
                        ? ((int) (measuredHeight + mAdjustedRowSpacing * (rowNum - 1)))
                        : (Math.min((int) (measuredHeight + mAdjustedRowSpacing * (rowNum - 1)),
                        heightSize));
            }
        }

        measuredWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : measuredWidth;
        measuredHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : measuredHeight;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int x = startRight ? (getWidth() - paddingRight) : paddingLeft;
        int y = paddingTop;

        int rowCount = mChildNumForRow.size(), childIdx = 0;
        for (int row = 0; row < rowCount; row++) {
            int childNum = mChildNumForRow.get(row);
            int rowHeight = mHeightForRow.get(row);
            float spacing = mHorizontalSpacingForRow.get(row);
            for (int i = 0; i < childNum && childIdx < getChildCount(); ) {
                View child = getChildAt(childIdx++);
                if (child.getVisibility() == GONE) {
                    continue;
                } else {
                    i++;
                }

                LayoutParams childParams = child.getLayoutParams();
                int marginLeft = 0, marginTop = 0, marginRight = 0;
                if (childParams instanceof MarginLayoutParams) {
                    MarginLayoutParams marginParams = (MarginLayoutParams) childParams;
                    marginLeft = marginParams.leftMargin;
                    marginRight = marginParams.rightMargin;
                    marginTop = marginParams.topMargin;
                }

                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                if (startRight) {
                    child.layout(x - marginRight - childWidth, y + marginTop,
                            x - marginRight, y + marginTop + childHeight);
                    x -= childWidth + spacing + marginLeft + marginRight;
                } else {
                    child.layout(x + marginLeft, y + marginTop,
                            x + marginLeft + childWidth, y + marginTop + childHeight);
                    x += childWidth + spacing + marginLeft + marginRight;
                }
            }
            x = startRight ? (getWidth() - paddingRight) : paddingLeft;
            y += rowHeight + mAdjustedRowSpacing;
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 返回是否在没有足够空间时允许子视图流到下一行。
     *
     * @return 是否在没有足够空间时将子视图流到下一行。
     */
    public boolean isFlow() {
        return mFlow;
    }

    /**
     * 设置当没有足够的空间时是否允许子视图流到下一行。
     *
     * @param flow true 允许流动。false将所有子视图限制在一行中。
     */
    public void setFlow(boolean flow) {
        mFlow = flow;
        requestLayout();
    }

    /**
     * 返回子视图之间的水平间距。
     *
     * @return 间距，可以是{@link FlowLayout#SPACING_AUTO}，也可以是固定的大小(以像素为单位)。
     */
    public int getChildSpacing() {
        return mChildSpacing;
    }

    /**
     * 设置子视图之间的水平间距。
     *
     * @param childSpacing 间距，可以是{@link FlowLayout#SPACING_AUTO}，也可以是固定的大小(以像素为单位)。
     */
    public void setChildSpacing(int childSpacing) {
        mChildSpacing = childSpacing;
        requestLayout();
    }

    /**
     * 返回最后一行的子视图之间的水平间距。
     *
     * @return 间距，可以是{@link FlowLayout#SPACING_AUTO}、{@link FlowLayout#SPACING_ALIGN}，也可以是固定的大小(以像素为单位)
     */
    public int getChildSpacingForLastRow() {
        return mChildSpacingForLastRow;
    }

    /**
     * 设置最后一行的子视图之间的水平间距。
     *
     * @param childSpacingForLastRow 间距，可以是{@link FlowLayout#SPACING_AUTO}、{@link FlowLayout#SPACING_ALIGN}，也可以是固定的大小(以像素为单位)
     */
    public void setChildSpacingForLastRow(int childSpacingForLastRow) {
        mChildSpacingForLastRow = childSpacingForLastRow;
        requestLayout();
    }

    /**
     * 返回行之间的垂直间距。
     *
     * @return 间距，可以是{@link FlowLayout#SPACING_AUTO}，也可以是固定的大小(以像素为单位)。
     */
    public float getRowSpacing() {
        return mRowSpacing;
    }

    /**
     * 设置行之间的垂直间距(以像素为单位)。使用SPACING_AUTO将所有行均匀地垂直放置。
     *
     * @param rowSpacing 间距，可以是{@link FlowLayout#SPACING_AUTO}，也可以是固定的大小(以像素为单位)。
     */
    public void setRowSpacing(float rowSpacing) {
        mRowSpacing = rowSpacing;
        requestLayout();
    }

    /**
     * 返回FlowLayout的最大行数。
     *
     * @return 最大行数
     */
    public int getMaxRows() {
        return mMaxRows;
    }

    /**
     * 将FlowLayout的行数设置为最大maxRows大小。
     *
     * @param maxRows 最大行数。
     */
    public void setMaxRows(int maxRows) {
        mMaxRows = maxRows;
        requestLayout();
    }

    /**
     * 获取子view之间间距
     *
     * @param spacingAttribute 子view间距
     * @param rowSize          总宽度
     * @param usedSize         已使用的宽度大小
     * @param childNum         当前行数的子view个数
     * @return
     */
    private float getSpacingForRow(int spacingAttribute, int rowSize, int usedSize, int childNum) {
        float spacing;
        if (spacingAttribute == SPACING_AUTO) {
            if (childNum > 1) {
                spacing = (rowSize - usedSize) / (childNum - 1);
            } else {
                spacing = 0;
            }
        } else {
            spacing = spacingAttribute;
        }
        return spacing;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}