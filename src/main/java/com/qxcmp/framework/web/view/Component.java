package com.qxcmp.framework.web.view;

/**
 * 页面组件基本类
 *
 * @author aaric
 */
public interface Component {

    /**
     * @return 该组件对应的渲染模板文件名称
     */
    String getFragmentFile();

    /**
     * @return 该组件对应的渲染模板片段名称
     */
    String getFragmentName();
}
