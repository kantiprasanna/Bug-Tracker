package com.kspk.bugtracker.controller;

import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kspk.bugtracker.DB;
import com.kspk.bugtracker.form.ChangePassword;
import com.kspk.bugtracker.form.User;
import com.kspk.bugtracker.repository.NotificationRepository;
import com.kspk.bugtracker.repository.UserRepository;

@Controller
public class ProfileController {

    private DB db;
    @Autowired
    public void setDb(DB db) { this.db = db; }

    private UserRepository ur;
    @Autowired
    public void setUr(UserRepository ur) { this.ur = ur; }

    private NotificationRepository nr;
    @Autowired
    public void setNr(NotificationRepository nr) { this.nr = nr; }

    @RequestMapping("/profile")
    public String profile(Model model, Principal principal) {
        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", "Profile | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("completedBugs", completedBugsCount(loggedUser.getId()));
        model.addAttribute("completedTasks", completedTasksCount(loggedUser.getId()));

        model.addAttribute("changePassword", new ChangePassword());

        return "profile";
    }

    @RequestMapping(value = "/password/change", method = RequestMethod.POST)
    public String changePw(@ModelAttribute ChangePassword cp, Principal principal) {
        User user = ur.findByEmail(principal.getName());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(!encoder.matches(cp.getOldPassword(), user.getPassword())) {
            return "redirect:/profile?wrongpassword";
        }

        if(!cp.getNewPassword().equals(cp.getNewPasswordAgain())) {
            return "redirect:/profile?notmatch";
        }

        ur.updatePassword(user.getId(), cp.getNewPassword());
        return "redirect:/profile?success";
    }

    private int completedBugsCount(long userId) {
        int res = 0;
        ResultSet rs = db.executeQuery("SELECT COUNT(bug_id) FROM bugs WHERE user_id = "+userId+" AND status = 'completed';");
        try {
            while(rs.next()) {
                res = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    private int completedTasksCount(long userId) {
        int res = 0;
        ResultSet rs = db.executeQuery("SELECT COUNT(task_id) FROM tasks WHERE user_id = "+userId+" AND status = 'completed';");
        try {
            while(rs.next()) {
                res = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

}
