package com.qxcmp.framework.web.view.elements.segment;

import com.qxcmp.framework.web.view.AbstractComponent;
import com.qxcmp.framework.web.view.support.Alignment;
import com.qxcmp.framework.web.view.support.Color;
import com.qxcmp.framework.web.view.support.Floated;
import lombok.Getter;
import lombok.Setter;

/**
 * 片段基类
 * <p>
 * 片段一般用来创建一组相关的内容
 *
 * @author aaric
 */
@Getter
@Setter
public class AbstractSegment extends AbstractComponent implements Segmentable {

    /**
     * 内容是否为禁用状态
     */
    private boolean disabled;

    /**
     * 是否为加载状态
     */
    private boolean loading;

    /**
     * 是否增加内边距
     */
    private boolean padded;

    /**
     * 是否增加更多内边距
     */
    private boolean veryPadded;

    /**
     * 是否为紧凑模式
     * <p>
     * 该模式的片段宽度为内容实际宽度
     */
    private boolean compact;

    /**
     * 颜色
     */
    private Color color = Color.NONE;

    /**
     * 是否为次要片段
     * <p>
     * 该属性将减少片段的显著性
     */
    private boolean secondary;

    /**
     * 是否为再次要片段
     * <p>
     * 该属性将更多的减少片段的显著性
     */
    private boolean tertiary;

    /**
     * 是否清楚浮动
     */
    private boolean clearing;

    /**
     * 浮动类型
     */
    private Floated floated = Floated.NONE;

    /**
     * 内容对齐方式
     */
    private Alignment alignment = Alignment.NONE;

    public AbstractSegment() {
        super("qxcmp/containers/segment");
    }

    @Override
    public String getFragmentName() {
        return "segment";
    }

    @Override
    public String getClassPrefix() {
        return "ui";
    }

    @Override
    public String getClassContent() {
        return "";
    }

    @Override
    public String getClassSuffix() {
        return "segment";
    }
}
