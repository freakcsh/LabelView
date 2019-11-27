package com.freak.label.view.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.freak.label.utils.ViewUtils;
import com.freak.label.widget.MaxHeightScrollView;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import java.util.Map;


public class LabelSelectScrollView extends MaxHeightScrollView implements LabelItemLayout.OnLabelItemSelectListener {
    private LinearLayout skuContainerLayout;
    private List<Sku> skuList;
    private List<SelectSkuAttribute> selectedAttributeList;  // 存放当前属性选中信息
    private OnSkuListener listener;                    // sku选中状态回调接口
    private List<BaseSkuAttribute> baseSkuAttributeList;

    public LabelSelectScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public LabelSelectScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setFillViewport(true);
        setOverScrollMode(OVER_SCROLL_NEVER);
        skuContainerLayout = new LinearLayout(context, attrs);
        skuContainerLayout.setId(ViewUtils.generateViewId());
        skuContainerLayout.setOrientation(LinearLayout.VERTICAL);
        skuContainerLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        addView(skuContainerLayout);
    }

    /**
     * 设置SkuView委托，MVVM + Databinding模式下使用
     *
     * @param delegate
     */
    public void setSkuViewDelegate(SkuViewDelegate delegate) {
        this.listener = delegate.getListener();
    }

    /**
     * 设置监听接口
     *
     * @param listener {@link OnSkuListener}
     */
    public void setListener(OnSkuListener listener) {
        this.listener = listener;
    }

    /**
     * 绑定sku数据
     *
     * @param skuList              组合列表
     * @param baseSkuAttributeList 规格集合
     */
    public void setSkuList(List<Sku> skuList, List<BaseSkuAttribute> baseSkuAttributeList) {
        this.skuList = skuList;
        this.baseSkuAttributeList = baseSkuAttributeList;
        // 清空sku视图
        skuContainerLayout.removeAllViews();

        // 获取分组的sku集合
        Map<String, List<SkuAttribute>> dataMap = getSkuGroupByName(baseSkuAttributeList);
        selectedAttributeList = new LinkedList<>();
        int index = 0;
        for (Iterator<Map.Entry<String, List<SkuAttribute>>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<SkuAttribute>> entry = it.next();

            // 构建sku视图
            SkuItemLayout itemLayout = new SkuItemLayout(getContext());
            itemLayout.setId(ViewUtils.generateViewId());
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            LogUtil.e(entry.getKey() + "   " + entry.getValue());
            itemLayout.buildItemLayout(index++, entry.getKey(), entry.getValue());
            itemLayout.setListener(this);
            skuContainerLayout.addView(itemLayout);
            // 初始状态下，所有属性信息设置为空
            selectedAttributeList.add(new SelectSkuAttribute(entry.getKey(), new SkuAttribute("", "")));
        }
        // 一个sku时，默认选中
        if (skuList.size() == 1) {
            selectedAttributeList.clear();
            //选中组合的ID组合
            String[] selectSkuId = this.skuList.get(0).getAttr_val_path().split(",");
            for (String id : selectSkuId) {
                for (BaseSkuAttribute baseSkuAttribute : baseSkuAttributeList) {
                    for (SkuAttribute attribute : baseSkuAttribute.getSku_value()) {
                        if (id.equals(StringUtils.toDecimalFormat3(Double.parseDouble(attribute.getVal_id())))) {
                            //匹配组合id，构建选中的组合
                            selectedAttributeList.add(new SelectSkuAttribute(baseSkuAttribute.getAttr_name(), attribute));
                        }
                    }
                }
            }
        }
        // 清除所有选中状态
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
    }

    /**
     * 将sku根据属性名进行分组
     *
     * @param list
     * @return 如{ "颜色": {"白色", "红色", "黑色"}, "尺寸": {"M", "L", "XL", "XXL"}}
     */
    private Map<String, List<SkuAttribute>> getSkuGroupByName(List<BaseSkuAttribute> list) {
        Map<String, List<SkuAttribute>> dataMap = new LinkedHashMap<>();
        for (BaseSkuAttribute sku : list) {
            //规格分类名字，例如：颜色
            String attributeName = sku.getAttr_name();
            for (SkuAttribute skuAttribute : sku.getSku_value()) {
                //属于该规格分类下的名字组合对象
                if (!dataMap.containsKey(attributeName)) {
                    dataMap.put(attributeName, new LinkedList<SkuAttribute>());
                }
                List<SkuAttribute> valueList = dataMap.get(attributeName);
                if (valueList != null && !valueList.contains(skuAttribute)) {
                    dataMap.get(attributeName).add(skuAttribute);
                }
            }
        }
        return dataMap;
    }

    /**
     * 重置所有属性的选中状态
     */
    private void clearAllLayoutStatus() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            itemLayout.clearItemViewStatus();
        }
    }

    /**
     * 设置所有属性的Enable状态，即是否可点击
     */
    private void optionLayoutEnableStatus() {
        int childCount = skuContainerLayout.getChildCount();
        if (childCount <= 1) {
            optionLayoutEnableStatusSingleProperty();
        } else {
            optionLayoutEnableStatusMultipleProperties();
        }
    }

    private void optionLayoutEnableStatusSingleProperty() {
        SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(0);
        // 遍历sku列表
        for (int i = 0; i < skuList.size(); i++) {
            // 属性值是否可点击flag
            Sku sku = skuList.get(i);
            String[] attributeBeanList = skuList.get(i).getAttr_val_path().split(",");
            List<SelectSkuAttribute> selectSkuAttributeList = new ArrayList<>();
            for (String id : attributeBeanList) {
                for (BaseSkuAttribute baseSkuAttribute : baseSkuAttributeList) {
                    for (SkuAttribute attribute : baseSkuAttribute.getSku_value()) {
                        if (id.equals(StringUtils.toDecimalFormat3(Double.parseDouble(attribute.getVal_id())))) {
                            //匹配组合id，构建选中的组合
                            selectSkuAttributeList.add(new SelectSkuAttribute(baseSkuAttribute.getAttr_name(), attribute));
                        }
                    }
                }
            }

            if (sku.getStock() > 0) {
                SkuAttribute attributeValue = selectSkuAttributeList.get(0).getSkuAttribute();
                itemLayout.optionItemViewEnableStatus(attributeValue);
            }
        }
    }

    private void optionLayoutEnableStatusMultipleProperties() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            // 遍历sku列表
            for (int j = 0; j < skuList.size(); j++) {
                // 属性值是否可点击flag
                boolean flag = false;
                Sku sku = skuList.get(j);
                String[] attributeBeanList = sku.getAttr_val_path().split(",");
                List<SelectSkuAttribute> selectSkuAttributeArrayList = new ArrayList<>();
                for (String id : attributeBeanList) {
                    for (BaseSkuAttribute baseSkuAttribute : baseSkuAttributeList) {
                        for (SkuAttribute skuAttribute : baseSkuAttribute.getSku_value()) {
                            if (id.equals(StringUtils.toDecimalFormat3(Double.parseDouble(skuAttribute.getVal_id())))) {
                                selectSkuAttributeArrayList.add(new SelectSkuAttribute(baseSkuAttribute.getAttr_name(), skuAttribute));
                            }
                        }
                    }
                }
                // 遍历选中信息列表
                for (int k = 0; k < selectedAttributeList.size(); k++) {
                    // i = k，跳过当前属性，避免多次设置是否可点击
                    if (i == k) continue;
                    // 选中信息为空，则说明未选中，无法判断是否有不可点击的情形，跳过
                    if (selectedAttributeList.get(k).getSkuAttribute() == null) continue;
                    // 选中信息列表中不包含当前sku的属性，则sku组合不存在，设置为不可点击
                    // 库存为0，设置为不可点击
                    if (!selectedAttributeList.get(k).getSkuAttribute().getAttr_name().equals(selectSkuAttributeArrayList.get(k).getSkuAttribute().getAttr_name())
                            || sku.getStock() == 0) {
                        flag = true;
                        break;
                    }
                }
                // flag 为false时，可点击
                if (!flag) {
                    SkuAttribute attributeValue = selectSkuAttributeArrayList.get(i).getSkuAttribute();
                    itemLayout.optionItemViewEnableStatus(attributeValue);
                }
            }
        }
    }

    /**
     * 设置所有属性的选中状态
     */
    private void optionLayoutSelectStatus() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            itemLayout.optionItemViewSelectStatus(selectedAttributeList.get(i));
        }
    }

    /**
     * 是否有sku选中
     *
     * @return
     */
    private boolean isSkuSelected() {
        for (SelectSkuAttribute attribute : selectedAttributeList) {
            if (attribute.getSkuAttribute() == null) {
                return false;
            }
            if (TextUtils.isEmpty(attribute.getSkuAttribute().getAttr_name())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取第一个未选中的属性名
     *
     * @return
     */
    public String getFirstUnelectedAttributeName() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            if (!itemLayout.isSelected()) {
                return itemLayout.getAttributeName();
            }
        }
        return "";
    }

    /**
     * 获取选中的Sku
     *
     * @return
     */
    public Sku getSelectedSku() {
        // 判断是否有选中的sku
        if (!isSkuSelected()) {
            return null;
        }
        for (Sku sku : skuList) {
            String[] attributeList = sku.getAttr_val_path().split(",");
            // 将sku的属性列表与selectedAttributeList匹配，完全匹配则为已选中sku
            List<SelectSkuAttribute> selectSkuAttributeArrayList = new ArrayList<>();
            for (String id : attributeList) {
                for (BaseSkuAttribute baseSkuAttribute : baseSkuAttributeList) {
                    for (SkuAttribute attribute : baseSkuAttribute.getSku_value()) {
                        if (id.equals(StringUtils.toDecimalFormat3(Double.parseDouble(attribute.getVal_id())))) {
                            //匹配组合id，构建选中的组合
                            selectSkuAttributeArrayList.add(new SelectSkuAttribute(baseSkuAttribute.getAttr_name(), attribute));
                        }
                    }
                }
            }
            boolean flag = true;
            for (int i = 0; i < selectSkuAttributeArrayList.size(); i++) {
                if (!isSameSkuAttribute(selectSkuAttributeArrayList.get(i), selectedAttributeList.get(i))) {
                    flag = false;
                }
            }
            if (flag) {
                return sku;
            }
        }
        return null;
    }

    /**
     * 设置选中的sku
     *
     * @param sku
     */
    public void setSelectedSku(Sku sku) {
        selectedAttributeList.clear();
        //选中组合的ID组合
        String[] selectSkuId = sku.getAttr_val_path().split(",");
        for (String id : selectSkuId) {
            for (BaseSkuAttribute baseSkuAttribute : baseSkuAttributeList) {
                for (SkuAttribute attribute : baseSkuAttribute.getSku_value()) {
                    if (id.equals(StringUtils.toDecimalFormat3(Double.parseDouble(attribute.getVal_id())))) {
                        selectedAttributeList.add(new SelectSkuAttribute(baseSkuAttribute.getAttr_name(), attribute));
                    }
                }
            }
        }
        // 清除所有选中状态
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
    }

    /**
     * 是否为同一个SkuAttribute
     *
     * @param previousAttribute
     * @param nextAttribute
     * @return
     */
    private boolean isSameSkuAttribute(SelectSkuAttribute previousAttribute, SelectSkuAttribute nextAttribute) {
        return previousAttribute.getSkuAttribute().getAttr_name().equals(nextAttribute.getSkuAttribute().getAttr_name())
                && previousAttribute.getKey().equals(nextAttribute.getKey());
    }

    @Override
    public void onSelect(int position, boolean selected, SelectSkuAttribute attribute) {
        if (selected) {
            // 选中，保存选中信息
            selectedAttributeList.set(position, attribute);
        } else {
            // 取消选中，清空保存的选中信息
            selectedAttributeList.get(position).setSkuAttribute(null);
        }
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
        // 回调接口
        if (isSkuSelected()) {
            listener.onSkuSelected(getSelectedSku());
        } else if (selected) {
            listener.onSelect(attribute);
        } else {
            listener.onUnselected(attribute);
        }
    }
}