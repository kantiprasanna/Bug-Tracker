package com.kspk.bugtracker.controller;

import java.security.Principal;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kspk.bugtracker.form.Notification;
import com.kspk.bugtracker.form.User;
import com.kspk.bugtracker.repository.NotificationRepository;
import com.kspk.bugtracker.repository.UserRepository;

@Controller
public class NotificationsController {

    private UserRepository ur;
    @Autowired
    public void setUr(UserRepository ur) {this.ur = ur;}

    private NotificationRepository nr;
    @Autowired
    public void setNr(NotificationRepository nr) {this.nr = nr; }

    @RequestMapping("/notifications")
    public String notifications(Model model, Principal principal) {
        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", "Notifications | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("notifications", nr.getAllNotifications(loggedUser.getId()));

        return "notifications";
    }

    @RequestMapping("/notification/{notificationId}")
    public String notification(Model model, Principal principal, @PathVariable(value="notificationId") String notificationId) {
        User user = ur.findByEmail(principal.getName());
        try {
            Notification n = nr.getNotificationById(Long.parseLong(notificationId));
            if(Objects.equals(n.getUserId(), user.getId())) {
                if(n != null) {
                    model.addAttribute("notification", n);

                    model.addAttribute("user", user);
                    model.addAttribute("isAdmin", ur.isAdmin(user));
                    model.addAttribute("pageTitle", "Notification #"+notificationId+" | Bug Tracker");
                    model.addAttribute("isUnread", nr.isThereUnread(user.getId()));

                    if(!n.isOpened()) nr.markAsRead(Long.parseLong(notificationId));
                    return "notification";
                }
                return "error";
            }
        } catch (Exception e) {
            return "error";
        }
        return "error";
    }

    @RequestMapping(value = "/notification/remove", method = RequestMethod.POST)
    public String removeNotification(@ModelAttribute Notification notification) {
        nr.removeNotification(notification.getNotificationId());
        return "redirect:/notifications";
    }

}
