package com.qxcmp.framework.news.web;

import com.google.common.collect.ImmutableList;
import com.qxcmp.framework.web.QXCMPBackendController;
import com.qxcmp.framework.web.view.views.Overview;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.qxcmp.framework.core.QXCMPConfiguration.QXCMP_BACKEND_URL;
import static com.qxcmp.framework.core.QXCMPNavigationConfiguration.NAVIGATION_ADMIN_NEWS;

@Controller
@RequestMapping(QXCMP_BACKEND_URL + "/news")
public class AdminNewsPageController extends QXCMPBackendController {

    @GetMapping("")
    public ModelAndView newsPage() {
        return page().addComponent(new Overview("新闻管理"))
                .setBreadcrumb("控制台", QXCMP_BACKEND_URL, "新闻管理")
                .setVerticalNavigation(NAVIGATION_ADMIN_NEWS, "")
                .build();
    }

    static List<String> getVerticalMenu(String activeItem) {
        return ImmutableList.of(activeItem, "我的文章", QXCMP_BACKEND_URL + "/news/user/article", "我的栏目", QXCMP_BACKEND_URL + "/news/user/channel", "文章管理", QXCMP_BACKEND_URL + "/news/article", "栏目管理", QXCMP_BACKEND_URL + "/news/channel");
    }

}