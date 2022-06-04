package com.kspk.bugtracker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kspk.bugtracker.form.Todo;
import com.kspk.bugtracker.form.User;
import com.kspk.bugtracker.repository.BugRepository;
import com.kspk.bugtracker.repository.NotificationRepository;
import com.kspk.bugtracker.repository.ProjectRepository;
import com.kspk.bugtracker.repository.TaskRepository;
import com.kspk.bugtracker.repository.TodoRepository;
import com.kspk.bugtracker.repository.UserRepository;

@Controller
public class DashboardController {

    private UserRepository ur;
    @Autowired
    public void setUr(UserRepository ur) { this.ur = ur; }

    private NotificationRepository nr;
    @Autowired
    public void setNr(NotificationRepository nr) { this.nr = nr; }

    private TodoRepository todor;
    @Autowired
    public void setTr(TodoRepository todor) { this.todor = todor; }

    private ProjectRepository pr;
    @Autowired
    public void setTodor(ProjectRepository pr) { this.pr = pr; }

    private TaskRepository tr;
    @Autowired
    public void setTr(TaskRepository tr) { this.tr = tr; }

    private BugRepository br;
    @Autowired
    public void setBr(BugRepository br) { this.br = br; }

    @RequestMapping("/")
    public String dashboard(Model model, Principal principal) {
        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", "Dashboard | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("projectsCount", pr.projectsCount(loggedUser.getId()));
        model.addAttribute("tasksCount", tr.tasksCount(loggedUser.getId()));
        model.addAttribute("bugsCount", br.bugsCount(loggedUser.getId()));
        model.addAttribute("todos", todor.getTodos(loggedUser.getId()));

        model.addAttribute("uncompletedBugs", br.uncompletedBugsCount(loggedUser.getId()));
        model.addAttribute("uncompletedTasks", tr.uncompletedTaskCount(loggedUser.getId()));

        model.addAttribute("projects", pr.getProjects(loggedUser.getId()));
        model.addAttribute("unreadNotification", nr.getUnreadNotifications(loggedUser.getId()));

        model.addAttribute("todo", new Todo());
        ur.updateLastLoginDate(loggedUser.getId());

        return "index";
    }

}
