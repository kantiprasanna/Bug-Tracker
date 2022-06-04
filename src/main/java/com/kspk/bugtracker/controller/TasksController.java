package com.kspk.bugtracker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kspk.bugtracker.DB;
import com.kspk.bugtracker.form.Task;
import com.kspk.bugtracker.form.User;
import com.kspk.bugtracker.repository.NotificationRepository;
import com.kspk.bugtracker.repository.ProjectRepository;
import com.kspk.bugtracker.repository.TaskRepository;
import com.kspk.bugtracker.repository.UserRepository;

@Controller
public class TasksController {

    private DB db;
    @Autowired
    public void setDb(DB db) { this.db = db; }

    private UserRepository ur;
    @Autowired
    public void setUr(UserRepository ur) { this.ur = ur; }

    private TaskRepository tr;
    @Autowired
    public void setTr(TaskRepository tr) { this.tr = tr; }

    private ProjectRepository pr;
    @Autowired
    public void setPr(ProjectRepository pr) { this.pr = pr; }

    private NotificationRepository nr;
    @Autowired
    public void setNr(NotificationRepository nr) { this.nr = nr; }

    @RequestMapping("/tasks")
    public String tasks(Model model, Principal principal) {
        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", "Tasks | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("allTask", tr.getAllTask(loggedUser.getId()));
        model.addAttribute("projects", pr.getAllProjects(loggedUser.getId()));

        model.addAttribute("task", new Task());

        return "tasks";
    }

    @RequestMapping("/tasks/project/{projectid}")
    public String projectTasks(Model model, Principal principal, @PathVariable(value="projectid") String projectid) {
        User loggedUser = ur.findByEmail(principal.getName());
        try {
            if(pr.isItTheirProject(Long.parseLong(projectid), loggedUser.getId())) {
                model.addAttribute("user", loggedUser);
                model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
                model.addAttribute("pageTitle", "Tasks | Bug Tracker");
                model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

                model.addAttribute("allTask", tr.getProjectTasks(Long.parseLong(projectid)));
                model.addAttribute("projects", pr.getAllProjects(loggedUser.getId()));

                model.addAttribute("task", new Task());

                return "tasks";
            } else {
                return "error";
            }
        } catch(Exception e) {
            return "error";
        }
    }

    @RequestMapping(value = "/tasks/add", method = RequestMethod.POST)
    public String addTask(@ModelAttribute Task task, Principal principal) {
        User sender = ur.findByEmail(principal.getName());

        tr.addTask(pr.getProjectIdByName(task.getProjectName()), sender.getId(), task.getTitle(), task.getDescription(), task.getStatus());

        return "redirect:/tasks";
    }

    @RequestMapping("/task/{taskid}")
    public String task(Model model, @PathVariable(value="taskid") String taskid, Principal principal) {
        User user = ur.findByEmail(principal.getName());
        try {
            if(tr.isItTheirTask(Long.parseLong(taskid), user.getId())) {
                Task task = tr.getTaskById(Long.parseLong(taskid));

                if(task != null) {
                    model.addAttribute("foundTask", task);

                    model.addAttribute("user", user);
                    model.addAttribute("isAdmin", ur.isAdmin(user));
                    model.addAttribute("pageTitle", "Task #"+taskid+" | Bug Tracker");
                    model.addAttribute("isUnread", nr.isThereUnread(user.getId()));
                    return "task";
                }
                return "error";
            }
        } catch (Exception e) {
            return "error";
        }
        return "error";
    }

    @RequestMapping("/task/{taskid}/edit")
    public String editTaskForm(Model model, @PathVariable(value="taskid") String taskid, Principal principal) {
        User user = ur.findByEmail(principal.getName());
        try {
            if(tr.isItTheirTask(Long.parseLong(taskid), user.getId())) {
                Task task = tr.getTaskById(Long.parseLong(taskid));

                if(task != null) {
                    model.addAttribute("task", task);

                    model.addAttribute("user", user);
                    model.addAttribute("isAdmin", ur.isAdmin(user));
                    model.addAttribute("pageTitle", "Task #"+taskid+" | Bug Tracker");
                    model.addAttribute("projects", pr.getProjects(user.getId()));
                    model.addAttribute("isUnread", nr.isThereUnread(user.getId()));
                    return "editTask";
                }
                return "error";
            }
        } catch (Exception e) {
            return "error";
        }
        return "error";
    }

    @RequestMapping(value = "/tasks/edit", method = RequestMethod.POST)
    public String editTask(@ModelAttribute Task task, Principal principal) {
        User user = ur.findByEmail(principal.getName());
        tr.updateTask(task.getTaskId(), pr.getProjectIdByName(task.getProjectName()), task.getTitle(), task.getDescription(), task.getStatus());

        return "redirect:/task/"+task.getTaskId();
    }

    @RequestMapping(value = "/task/delete", method = RequestMethod.POST)
    public String deleteTask(@ModelAttribute Task task) {
        Task t = tr.getTaskById(task.getTaskId());
        tr.deleteTask(t.getTaskId());
        return "redirect:/tasks";
    }

}
