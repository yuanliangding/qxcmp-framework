package com.qxcmp.admin.page;

import com.qxcmp.message.InnerMessageService;
import com.qxcmp.message.SiteNotification;
import com.qxcmp.message.SiteNotificationService;
import com.qxcmp.user.User;
import com.qxcmp.web.view.Component;
import com.qxcmp.web.view.elements.breadcrumb.Breadcrumb;
import com.qxcmp.web.view.elements.breadcrumb.BreadcrumbItem;
import com.qxcmp.web.view.elements.container.Container;
import com.qxcmp.web.view.elements.grid.AbstractGrid;
import com.qxcmp.web.view.elements.grid.Col;
import com.qxcmp.web.view.elements.grid.Row;
import com.qxcmp.web.view.elements.grid.VerticallyDividedGrid;
import com.qxcmp.web.view.elements.label.AbstractLabel;
import com.qxcmp.web.view.elements.label.Label;
import com.qxcmp.web.view.elements.menu.Menu;
import com.qxcmp.web.view.elements.menu.RightMenu;
import com.qxcmp.web.view.elements.menu.VerticalMenu;
import com.qxcmp.web.view.elements.menu.VerticalSubMenu;
import com.qxcmp.web.view.elements.menu.item.*;
import com.qxcmp.web.view.elements.message.*;
import com.qxcmp.web.view.modules.accordion.AccordionItem;
import com.qxcmp.web.view.modules.sidebar.AbstractSidebar;
import com.qxcmp.web.view.modules.sidebar.AccordionMenuSidebar;
import com.qxcmp.web.view.page.AbstractPage;
import com.qxcmp.web.view.page.AbstractQxcmpPage;
import com.qxcmp.web.view.support.Color;
import com.qxcmp.web.view.support.Fixed;
import com.qxcmp.web.view.support.Wide;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.function.Supplier;

import static com.qxcmp.admin.QxcmpAdminModule.ADMIN_URL;
import static com.qxcmp.core.QxcmpNavigationConfiguration.NAVIGATION_ADMIN_PROFILE;
import static com.qxcmp.core.QxcmpNavigationConfiguration.NAVIGATION_ADMIN_SIDEBAR;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * 后台页面抽象类
 *
 * @author Aaric
 */
@Scope(SCOPE_PROTOTYPE)
@org.springframework.stereotype.Component
@RequiredArgsConstructor
public abstract class AbstractQxcmpAdminPage extends AbstractQxcmpPage {

    private AbstractSidebar sidebar = new AccordionMenuSidebar().setAttachEventsSelector(".ui.bottom.fixed.menu .sidebar.item");
    private VerticalMenu verticalMenu;
    private Col content = new Col(Wide.SIXTEEN);

    private InnerMessageService innerMessageService;
    private SiteNotificationService siteNotificationService;

    @Override
    public AbstractPage addComponent(Component component) {

        if (Objects.nonNull(component)) {
            content.addComponent(component);
        }

        return this;
    }

    @Override
    public AbstractPage addComponent(Supplier<Component> supplier) {
        return addComponent(supplier.get());
    }

    @Override
    public AbstractPage addComponents(Collection<Component> components) {
        content.addComponents(components);
        return this;
    }

    /**
     * 设置后台页面面包屑导航
     *
     * @return 如果返回空则不生成面包屑导航
     */
    protected List<String> getBreadcrumb() {
        return Collections.emptyList();
    }

    /**
     * 设置页面菜单
     *
     * @param id       导航ID
     * @param activeId 当前激活的导航ID
     *
     * @return 后台页面
     */
    public AbstractQxcmpAdminPage setVerticalMenu(String id, String activeId) {

        VerticalMenu verticalMenu = new VerticalMenu().setFluid();
        verticalMenu.setTabular();

        navigationService.get(id).getItems().forEach(navigation -> {
            if (navigation.isVisible(userService.currentUser())) {
                if (navigation.getItems().isEmpty()) {
                    TextItem textItem = new TextItem(navigation.getTitle(), navigation.getAnchor().getHref());

                    if (StringUtils.equals(activeId, navigation.getId())) {
                        textItem.setActive();
                    }

                    textItem.addContext("navigation-id", navigation.getId());

                    verticalMenu.addItem(textItem);
                }
            }
        });

        if (!verticalMenu.getItems().isEmpty()) {
            this.verticalMenu = verticalMenu;
        }

        return this;
    }

    public AbstractQxcmpAdminPage setVerticalMenuBadge(String id, String text) {
        return setVerticalMenuBadge(id, text, Color.NONE);
    }

    public AbstractQxcmpAdminPage setVerticalMenuBadge(String id, String text, Color color) {
        return setVerticalMenuBadge(id, new Label(text).setColor(color));
    }

    /**
     * 设置页面菜单项徽章
     *
     * @param id    导航ID
     * @param label 徽章
     *
     * @return 后台页面
     */
    public AbstractQxcmpAdminPage setVerticalMenuBadge(String id, AbstractLabel label) {

        if (Objects.nonNull(verticalMenu)) {
            verticalMenu.getItems().forEach(menuItem -> {
                if (Objects.nonNull(menuItem.getContext("navigation-id")) && StringUtils.equals(menuItem.getContext("navigation-id").toString(), id)) {
                    if (menuItem instanceof TextItem) {
                        TextItem textItem = (TextItem) menuItem;
                        textItem.setBadge(label);
                    }
                }
            });
        }

        return this;
    }

    @Override
    public ModelAndView build() {
        buildSidebar();
        buildPageContent();
        super.addComponent(sidebar);
        return super.build();
    }

    private void buildSidebar() {
        final User user = userService.currentUser();
        buildSidebarTopFixedMenu(user);
        buildSidebarMenu();
        buildSideBottomFixedMenu(user);
    }

    private void buildSidebarTopFixedMenu(User user) {
        final Menu menu = new Menu();
        menu.setInverted().setFixed(Fixed.TOP);
        menu.addItem(new LogoImageItem(siteService.getLogo(), siteService.getTitle()));
        RightMenu rightMenu = new RightMenu();
        rightMenu.addItem(new BackendAccountAlarmItem(innerMessageService.countByUserId(user.getId())));
        rightMenu.addItem(new BackendAccountMenuItem(user, navigationService.get(NAVIGATION_ADMIN_PROFILE).getItems()));
        menu.setRightMenu(rightMenu);
        sidebar.setTopFixedMenu(menu);
    }

    private void buildSidebarMenu() {
        final Menu menu = new Menu();
        menu.setInverted().setFixed(Fixed.BOTTOM);
        menu.addItem(new SidebarIconItem());
        RightMenu rightMenu = new RightMenu();
        rightMenu.addItem(new TextItem("关于", ADMIN_URL + "/about"));
        menu.setRightMenu(rightMenu);
        sidebar.setBottomFixedMenu(menu);
    }

    private void buildSideBottomFixedMenu(User user) {
        navigationService.get(NAVIGATION_ADMIN_SIDEBAR).getItems().stream()
                .filter(navigation -> navigation.isVisible(user))
                .forEach(navigation -> {
                    if (navigation.getItems().isEmpty()) {
                        if (Objects.nonNull(navigation.getIcon())) {
                            sidebar.addSideContent(new LabeledIconItem(navigation.getTitle(), navigation.getIcon(), navigation.getAnchor()));
                        } else {
                            sidebar.addSideContent(new TextItem(navigation.getTitle(), navigation.getAnchor().getHref()).setLink());
                        }
                    } else {
                        if (navigation.getItems().stream().anyMatch(n -> n.isVisible(user))) {

                            VerticalSubMenu verticalMenu = new VerticalSubMenu();

                            navigation.getItems().forEach(item -> {
                                if (item.isVisible(user)) {
                                    verticalMenu.addItem(new TextItem(item.getTitle(), item.getAnchor().getHref()));
                                }
                            });

                            AccordionItem accordionItem = new AccordionItem();
                            accordionItem.setTitle(navigation.getTitle());
                            accordionItem.setContent(verticalMenu);

                            sidebar.addSideContent(new AccordionMenuItem(accordionItem).setLink());
                        }
                    }
                });
    }

    private void buildPageContent() {
        final Container container = new Container();
        final AbstractGrid grid = new VerticallyDividedGrid().setVerticallyPadded();
        final Row contentRow = new Row();

        AbstractMessage message = buildSiteNotification();

        if (Objects.nonNull(message)) {
            container.addComponent(message);
        }

        buildPageBreadcrumb(grid);

        if (Objects.nonNull(verticalMenu)) {
            contentRow.addCol(new Col().setComputerWide(Wide.THREE).setMobileWide(Wide.SIXTEEN).addComponent(verticalMenu));
            contentRow.addCol(content.setComputerWide(Wide.THIRTEEN).setMobileWide(Wide.SIXTEEN));
        } else {
            contentRow.addCol(content.setGeneralWide(Wide.SIXTEEN));
        }

        grid.addItem(contentRow);
        container.addComponent(grid);
        sidebar.addContent(container);
    }

    private void buildPageBreadcrumb(AbstractGrid grid) {
        List<String> contents = getBreadcrumb();
        if (Objects.nonNull(contents) && !contents.isEmpty()) {
            Breadcrumb breadcrumb = new Breadcrumb();
            for (int i = 0; i < contents.size(); i += 2) {
                String text = contents.get(i);
                if (i + 1 == contents.size()) {
                    breadcrumb.addItem(new BreadcrumbItem(text));
                } else {
                    String url = contents.get(i + 1);
                    if (Objects.nonNull(url)) {
                        breadcrumb.addItem(new BreadcrumbItem(text, ADMIN_URL + "/" + url));
                    } else {
                        breadcrumb.addItem(new BreadcrumbItem(text));
                    }
                }
            }
            grid.addItem(new Row().addCol(new Col(Wide.SIXTEEN).addComponent(breadcrumb)));
        }
    }

    private AbstractMessage buildSiteNotification() {

        AbstractMessage message = null;

        Optional<SiteNotification> activeNotifications = siteNotificationService.findActiveNotifications();

        if (activeNotifications.isPresent()) {

            SiteNotification siteNotification = activeNotifications.get();

            switch (siteNotification.getType()) {
                case "网站通知":
                    message = new InfoMessage(siteNotification.getTitle(), siteNotification.getContent());
                    break;
                case "网站警告":
                    message = new WarningMessage(siteNotification.getTitle(), siteNotification.getContent());
                    break;
                case "网站错误":
                    message = new ErrorMessage(siteNotification.getTitle(), siteNotification.getContent());
                    break;
                default:
                    message = new Message(siteNotification.getTitle(), siteNotification.getContent());
            }

            message.setCloseable(true);
        }

        return message;
    }

    @Autowired
    public void setInnerMessageService(InnerMessageService innerMessageService) {
        this.innerMessageService = innerMessageService;
    }

    @Autowired
    public void setSiteNotificationService(SiteNotificationService siteNotificationService) {
        this.siteNotificationService = siteNotificationService;
    }
}
