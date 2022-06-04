package com.kspk.bugtracker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kspk.bugtracker.form.User;
import com.kspk.bugtracker.repository.BugRepository;
import com.kspk.bugtracker.repository.NotificationRepository;
import com.kspk.bugtracker.repository.TaskRepository;
import com.kspk.bugtracker.repository.UserRepository;

@Controller
public class EmployeeController {

    private UserRepository ur;
    @Autowired
    public void setUr(UserRepository ur) { this.ur = ur; }

    private NotificationRepository nr;
    @Autowired
    public void setNr(NotificationRepository nr) { this.nr = nr; }

    private BugRepository br;
    @Autowired
    public void setBr(BugRepository br) { this.br = br; }

    private TaskRepository tr;
    @Autowired
    public void setTr(TaskRepository tr) { this.tr = tr; }

    @RequestMapping("/employees")
    public String employees(Model model, Principal principal) {
        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", "Employees | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("employee", new User());
        model.addAttribute("allUsers", ur.getAllUser());

        return "employees";
    }

    @RequestMapping("/employees/admin")
    public String admins(Model model, Principal principal) {
        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", "Employees | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("employee", new User());
        model.addAttribute("allUsers", ur.getAllUser());

        return "admins";
    }

    @RequestMapping("/employees/employee")
    public String filteredEmployees(Model model, Principal principal) {
        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", "Employees | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("employee", new User());
        model.addAttribute("allUsers", ur.getAllUser());

        return "filteredEmployees";
    }

    @RequestMapping(value = "/admin/employee/add", method = RequestMethod.POST)
    public String addUser(@ModelAttribute User user) {
        User found = ur.findByEmail(user.getEmail());
        if(found != null) {
            return "redirect:/employees?emailTaken";
        }

        ur.addUser(user.getFname(), user.getLname(), user.getEmail(), user.getPassword());
        ur.addRole(ur.findByEmail(user.getEmail()).getId(), Long.parseLong("1"));

        return "redirect:/employees";
    }

    @RequestMapping("/employee/{email}")
    public String employee(Model model, Principal principal, @PathVariable(value="email") String email) {
        User foundUser = ur.findByEmail(email);
        if(foundUser == null) {
            return "error";
        }

        User loggedUser = ur.findByEmail(principal.getName());
        model.addAttribute("user", loggedUser);
        model.addAttribute("isAdmin", ur.isAdmin(loggedUser));
        model.addAttribute("pageTitle", email+" | Bug Tracker");
        model.addAttribute("isUnread", nr.isThereUnread(loggedUser.getId()));

        model.addAttribute("foundEmployee", foundUser);
        model.addAttribute("totalBugs", br.getBugCount(foundUser.getId()));
        model.addAttribute("totalTasks", tr.getTaskCount(foundUser.getId()));

        return "employee";
    }

}
