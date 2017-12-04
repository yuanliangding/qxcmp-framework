package com.qxcmp.framework.link.event;

import com.qxcmp.framework.config.SiteService;
import com.qxcmp.framework.link.Link;
import com.qxcmp.framework.message.MessageService;
import com.qxcmp.framework.user.User;
import com.qxcmp.framework.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.qxcmp.framework.core.QxcmpSecurityConfiguration.PRIVILEGE_ADMIN_LINK;

/**
 * @author Aaric
 */
@Component
@RequiredArgsConstructor
public class AdminLinkEventListener {

    private final MessageService messageService;
    private final UserService userService;
    private final SiteService siteService;

    @EventListener
    public void onNewEvent(AdminLinkNewEvent event) {
        Link link = event.getLink();
        List<User> feedUsers = userService.findByAuthority(PRIVILEGE_ADMIN_LINK);
        feedUsers.add(event.getTarget());

        messageService.feed(feedUsers.stream().map(User::getId).collect(Collectors.toList()), event.getTarget(),
                String.format("%s 新建了一个链接 <a href='https://%s/admin/link/%d/edit'>%s</a>",
                        event.getTarget().getDisplayName(),
                        siteService.getDomain(),
                        link.getId(),
                        link.getTitle()));
    }

    @EventListener
    public void onEditEvent(AdminLinkEditEvent event) {
        Link link = event.getLink();
        List<User> feedUsers = userService.findByAuthority(PRIVILEGE_ADMIN_LINK);
        feedUsers.add(event.getTarget());

        messageService.feed(feedUsers.stream().map(User::getId).collect(Collectors.toList()), event.getTarget(),
                String.format("%s 编辑了一个链接 <a href='https://%s/admin/link/%d/edit'>%s</a>",
                        event.getTarget().getDisplayName(),
                        siteService.getDomain(),
                        link.getId(),
                        link.getTitle()));
    }

    @EventListener
    public void onSettingsEvent(AdminLinkSettingsEvent event) {
        List<User> feedUsers = userService.findByAuthority(PRIVILEGE_ADMIN_LINK);
        feedUsers.add(event.getTarget());

        messageService.feed(feedUsers.stream().map(User::getId).collect(Collectors.toList()), event.getTarget(),
                String.format("%s 修改了 <a href='https://%s/admin/link/settings'>链接配置</a>",
                        event.getTarget().getDisplayName(),
                        siteService.getDomain()));
    }

}
