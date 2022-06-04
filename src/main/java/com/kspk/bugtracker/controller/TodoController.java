package com.kspk.bugtracker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kspk.bugtracker.form.Todo;
import com.kspk.bugtracker.form.User;
import com.kspk.bugtracker.repository.TodoRepository;
import com.kspk.bugtracker.repository.UserRepository;

@Controller
public class TodoController {

    private TodoRepository todoRepository;
    @Autowired
    public void setTodoRepository(TodoRepository todoRepository) { this.todoRepository = todoRepository; }

    private UserRepository userRepository;
    @Autowired
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }

    @RequestMapping(value = "/addtodo", method = RequestMethod.POST)
    public String handleTodo(@ModelAttribute Todo todo, Principal principal) {
        User sender = userRepository.findByEmail(principal.getName());
        todoRepository.addTodo(todo.getTask(), sender.getId());

        return "redirect:/";
    }

}
