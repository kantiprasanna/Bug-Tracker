package com.kspk.bugtracker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kspk.bugtracker.form.Project;
import com.kspk.bugtracker.form.User;
import com.kspk.bugtracker.repository.NotificationRepository;
import com.kspk.bugtracker.repository.ProjectRepository;
import com.kspk.bugtracker.repository.UserRepository;

@Controller
public class ProjectsController {

    private UserRepository ur;
    @Autowired
    public void setUr(UserRepository ur) { this.ur = ur; }

    private ProjectRepository pr;
    @Autowired
    public void setPr(ProjectRepository pr) { this.pr = pr; }

    private NotificationRepository nr;
    @Autowired
    public void setNr(NotificationRepository nr) { this.nr = nr; }

    @RequestMapping("/projects")
    public String projects(Model model, Principal principal) {
        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", "Projects | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("projects", pr.getAllProjects(loggedUser.getId()));
        model.addAttribute("project", new Project());

        return "projects";
    }

    @RequestMapping(value = "/projects/add", method = RequestMethod.POST)
    public String addProject(@ModelAttribute Project project, Principal principal) {
        User sender = ur.findByEmail(principal.getName());
        pr.addToProjects(sender.getId(), project.getTitle(), project.getDescription(), project.getReadme());

        return "redirect:/projects";
    }

    @RequestMapping("/project/{projectid}")
    public String project(Model model, @PathVariable(value="projectid") String projectid, Principal principal) {
        User user = ur.findByEmail(principal.getName());
        try {
            if(pr.isItTheirProject(Long.parseLong(projectid), user.getId())) {
                Project project = pr.getProject(Long.parseLong(projectid));

                if(project != null) {
                    model.addAttribute("foundProject", project);

                    model.addAttribute("user", user);
                    model.addAttribute("isAdmin", ur.isAdmin(user));
                    model.addAttribute("pageTitle", "Project #"+projectid+" | Bug Tracker");
                    model.addAttribute("isUnread", nr.isThereUnread(user.getId()));
                    return "project";
                }
                return "error";
            }
        } catch (Exception e) {
            return "error";
        }
        return "error";
    }

    @RequestMapping("/project/{projectid}/edit")
    public String editProjectForm(Model model, @PathVariable(value="projectid") String projectid, Principal principal) {
        User user = ur.findByEmail(principal.getName());
        try {
            if(pr.isItTheirProject(Long.parseLong(projectid), user.getId())) {
                Project project = pr.getProject(Long.parseLong(projectid));

                if(project != null) {
                    model.addAttribute("project", project);

                    model.addAttribute("user", user);
                    model.addAttribute("isAdmin", ur.isAdmin(user));
                    model.addAttribute("pageTitle", "Project #"+projectid+" | Bug Tracker");
                    model.addAttribute("projects", pr.getAllProjects(user.getId()));
                    model.addAttribute("isUnread", nr.isThereUnread(user.getId()));
                    return "editProject";
                }
                return "error";
            }
        } catch (Exception e) {
            return "error";
        }
        return "error";
    }

    @RequestMapping(value = "/projects/edit", method = RequestMethod.POST)
    public String editProject(@ModelAttribute Project project, Principal principal) {
        User user = ur.findByEmail(principal.getName());
        pr.updateProject(project.getTitle(), project.getDescription(), project.getReadme(), project.getProjectId());

        return "redirect:/project/"+project.getProjectId();
    }

    @RequestMapping(value = "/project/delete", method = RequestMethod.POST)
    public String deleteProject(@ModelAttribute Project project) {
        Project p = pr.getProject(project.getProjectId());
        pr.deleteProject(p.getProjectId());
        return "redirect:/projects";
    }

}
