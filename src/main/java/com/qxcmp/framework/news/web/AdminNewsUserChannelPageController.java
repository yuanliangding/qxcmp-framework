package com.qxcmp.framework.news.web;

import com.qxcmp.framework.audit.ActionException;
import com.qxcmp.framework.news.ArticleService;
import com.qxcmp.framework.news.Channel;
import com.qxcmp.framework.news.ChannelService;
import com.qxcmp.framework.user.User;
import com.qxcmp.framework.web.QXCMPBackendController;
import com.qxcmp.framework.web.view.elements.header.IconHeader;
import com.qxcmp.framework.web.view.elements.html.HtmlText;
import com.qxcmp.framework.web.view.elements.icon.Icon;
import com.qxcmp.framework.web.view.elements.segment.Segment;
import com.qxcmp.framework.web.view.support.Alignment;
import com.qxcmp.framework.web.view.support.utils.TableHelper;
import com.qxcmp.framework.web.view.views.Overview;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

import static com.qxcmp.framework.core.QXCMPConfiguration.QXCMP_BACKEND_URL;
import static com.qxcmp.framework.news.web.AdminNewsPageController.getVerticalMenu;

@Controller
@RequestMapping(QXCMP_BACKEND_URL + "/news/user/channel")
@RequiredArgsConstructor
public class AdminNewsUserChannelPageController extends QXCMPBackendController {

    private final ChannelService channelService;

    private final ArticleService articleService;

    private final TableHelper tableHelper;

    @GetMapping("")
    public ModelAndView userChannelPage(Pageable pageable) {

        User user = currentUser().orElseThrow(RuntimeException::new);

        Page<Channel> channels = channelService.findByUser(user, pageable);

        return page().addComponent(tableHelper.convert("user", Channel.class, channels))
                .setBreadcrumb("控制台", QXCMP_BACKEND_URL, "新闻管理", QXCMP_BACKEND_URL + "/news", "我的栏目")
                .setVerticalMenu(getVerticalMenu("我的栏目"))
                .build();
    }

    @GetMapping("/{id}/details")
    public ModelAndView userChannelDetailsPage(@PathVariable String id) {

        User user = currentUser().orElseThrow(RuntimeException::new);

        List<Channel> channels = channelService.findByUser(user);

        return channelService.findOne(id)
                .filter(channels::contains)
                .map(channel -> page().addComponent(new Overview(channel.getName()).setAlignment(Alignment.CENTER)
                        .addComponent(new HtmlText(channel.getContent()))
                        .addLink("返回", QXCMP_BACKEND_URL + "/news/user/channel"))
                        .setBreadcrumb("控制台", QXCMP_BACKEND_URL, "新闻管理", QXCMP_BACKEND_URL + "/news", "我的栏目", QXCMP_BACKEND_URL + "/news/user/channel", "栏目查看")
                        .setVerticalMenu(getVerticalMenu("我的栏目"))
                        .build()).orElse(overviewPage(new Overview(new IconHeader("栏目不存在", new Icon("warning circle"))).addLink("返回", QXCMP_BACKEND_URL + "/news/user/channel")).build());
    }

    @GetMapping("/{id}/edit")
    public ModelAndView userChannelEditPage(@PathVariable String id) {

        User user = currentUser().orElseThrow(RuntimeException::new);

        List<Channel> channels = channelService.findByUser(user);

        return channelService.findOne(id)
                .filter(channels::contains)
                .map(channel -> {

                    Object form;

                    if (StringUtils.equals(channel.getOwner().getId(), user.getId())) {
                        form = new AdminNewsUserChannelOwnerEditForm();
                        AdminNewsUserChannelOwnerEditForm editForm = (AdminNewsUserChannelOwnerEditForm) form;
                        editForm.setId(channel.getId());
                        editForm.setCover(channel.getCover());
                        editForm.setName(channel.getName());
                        editForm.setDescription(channel.getDescription());
                        editForm.setOwner(channel.getOwner());
                        editForm.setAdmins(channel.getAdmins());
                        editForm.setContent(channel.getContent());
                        editForm.setContentQuill(channel.getContentQuill());
                    } else {
                        form = new AdminNewsUserChannelAdminEditForm();
                        AdminNewsUserChannelAdminEditForm editForm = (AdminNewsUserChannelAdminEditForm) form;
                        editForm.setId(channel.getId());
                        editForm.setCover(channel.getCover());
                        editForm.setName(channel.getName());
                        editForm.setDescription(channel.getDescription());
                        editForm.setAdmins(channel.getAdmins());
                        editForm.setContent(channel.getContent());
                        editForm.setContentQuill(channel.getContentQuill());
                    }

                    return page().addComponent(new Segment().addComponent(convertToForm(form)))
                            .setBreadcrumb("控制台", QXCMP_BACKEND_URL, "新闻管理", QXCMP_BACKEND_URL + "/news", "我的栏目", QXCMP_BACKEND_URL + "/news/user/channel", "栏目管理")
                            .setVerticalMenu(getVerticalMenu("我的栏目"))
                            .addObject(form)
                            .addObject("selection_items_owner", userService.findAll())
                            .addObject("selection_items_admins", userService.findAll())
                            .build();
                }).orElse(overviewPage(new Overview(new IconHeader("栏目不存在", new Icon("warning circle"))).addLink("返回", QXCMP_BACKEND_URL + "/news/user/channel")).build());
    }

    @PostMapping("/owner/edit")
    public ModelAndView userChannelOwnerPage(@Valid final AdminNewsUserChannelOwnerEditForm form, BindingResult bindingResult) {

        User user = currentUser().orElseThrow(RuntimeException::new);

        List<Channel> channels = channelService.findByOwner(user);

        return channelService.findOne(form.getId())
                .filter(channels::contains)
                .map(channel -> {

                    if (bindingResult.hasErrors()) {
                        return page().addComponent(new Segment().addComponent(convertToForm(form).setErrorMessage(convertToErrorMessage(bindingResult, form))))
                                .setBreadcrumb("控制台", QXCMP_BACKEND_URL, "新闻管理", QXCMP_BACKEND_URL + "/news", "我的栏目", QXCMP_BACKEND_URL + "/news/user/channel", "栏目管理")
                                .setVerticalMenu(getVerticalMenu("我的栏目"))
                                .addObject(form)
                                .addObject("selection_items_owner", userService.findAll())
                                .addObject("selection_items_admins", userService.findAll())
                                .build();
                    }

                    return submitForm(form, context -> {
                        try {
                            channelService.update(channel.getId(), c -> {
                                c.setCover(form.getCover());
                                c.setName(form.getName());
                                c.setDescription(form.getDescription());
                                c.setOwner(form.getOwner());
                                c.setAdmins(form.getAdmins());
                                c.setContent(form.getContent());
                                c.setContentQuill(form.getContentQuill());
                            });
                        } catch (Exception e) {
                            throw new ActionException(e.getMessage(), e);
                        }
                    }, (stringObjectMap, overview) -> overview.addLink("返回", QXCMP_BACKEND_URL + "/news/user/channel"));
                }).orElse(overviewPage(new Overview(new IconHeader("栏目不存在", new Icon("warning circle"))).addLink("返回", QXCMP_BACKEND_URL + "/news/user/channel")).build());
    }

    @PostMapping("/admin/edit")
    public ModelAndView userChannelAdminPage(@Valid final AdminNewsUserChannelAdminEditForm form, BindingResult bindingResult) {

        User user = currentUser().orElseThrow(RuntimeException::new);

        List<Channel> channels = channelService.findByAdmin(user);

        return channelService.findOne(form.getId())
                .filter(channels::contains)
                .map(channel -> {

                    if (bindingResult.hasErrors()) {
                        return page().addComponent(new Segment().addComponent(convertToForm(form).setErrorMessage(convertToErrorMessage(bindingResult, form))))
                                .setBreadcrumb("控制台", QXCMP_BACKEND_URL, "新闻管理", QXCMP_BACKEND_URL + "/news", "我的栏目", QXCMP_BACKEND_URL + "/news/user/channel", "栏目管理")
                                .setVerticalMenu(getVerticalMenu("我的栏目"))
                                .addObject(form)
                                .addObject("selection_items_owner", userService.findAll())
                                .addObject("selection_items_admins", userService.findAll())
                                .build();
                    }

                    return submitForm(form, context -> {
                        try {
                            channelService.update(channel.getId(), c -> {
                                c.setCover(form.getCover());
                                c.setName(form.getName());
                                c.setDescription(form.getDescription());
                                c.setAdmins(form.getAdmins());
                                c.setContent(form.getContent());
                                c.setContentQuill(form.getContentQuill());
                            });
                        } catch (Exception e) {
                            throw new ActionException(e.getMessage(), e);
                        }
                    }, (stringObjectMap, overview) -> overview.addLink("返回", QXCMP_BACKEND_URL + "/news/user/channel"));
                }).orElse(overviewPage(new Overview(new IconHeader("栏目不存在", new Icon("warning circle"))).addLink("返回", QXCMP_BACKEND_URL + "/news/user/channel")).build());
    }
}
