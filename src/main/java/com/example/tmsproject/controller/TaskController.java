package com.example.tmsproject.controller;

import com.example.tmsproject.model.Task;
import com.example.tmsproject.model.User;
import com.example.tmsproject.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "admin-dashboard";
    }

    @GetMapping("/admin/tasks/add")
    public String addTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "add-task";
    }

    @PostMapping("/admin/tasks")
    public String saveTask(Task task) {
        task.setStatus("Available");
        taskService.saveTask(task);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "user-dashboard";
    }

    @GetMapping("/user/tasks/pick/{id}")
    public String pickTask(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        Optional<Task> opt = taskService.getTaskById(id);
        if (opt.isPresent()) {
            Task task = opt.get();
            task.setAssignedTo(user);
            task.setStatus("In Progress");
            taskService.saveTask(task);
        }
        return "redirect:/user/dashboard";
    }

    @GetMapping("/user/tasks/my")
    public String myTasks(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        model.addAttribute("tasks", taskService.getTasksByUser(user));
        return "my-tasks";
    }

    @GetMapping("/user/tasks/complete/{id}")
    public String completeForm(@PathVariable Long id, Model model) {
        taskService.getTaskById(id).ifPresent(task -> model.addAttribute("task", task));
        return "request-completion";
    }

    @PostMapping("/user/tasks/complete/{id}")
    public String submitCompletion(
            @PathVariable Long id,
            @RequestParam("reason") String reason) {
        taskService.getTaskById(id).ifPresent(task -> {
            task.setStatus("Completed");
            task.setCompletionReason(reason);
            taskService.saveTask(task);
        });
        return "redirect:/user/tasks/my";
    }

    @GetMapping("/user/tasks/extend/{id}")
    public String extendRequestForm(@PathVariable Long id, Model model) {
        taskService.getTaskById(id).ifPresent(task -> model.addAttribute("task", task));
        return "request-extension";
    }

    @PostMapping("/user/tasks/extend/{id}")
    public String submitExtensionRequest(
            @PathVariable Long id,
            @RequestParam("reason") String reason) {
        taskService.getTaskById(id).ifPresent(task -> {
            task.setStatus("Extension Requested");
            task.setExtensionReason(reason);
            taskService.saveTask(task);
        });
        return "redirect:/user/tasks/my";
    }

    @GetMapping("/admin/tasks/edit/{id}")
    public String editDeadlineForm(@PathVariable Long id, Model model) {
        Optional<Task> opt = taskService.getTaskById(id);
        if (opt.isPresent()) {
            model.addAttribute("task", opt.get());
            return "edit-deadline";
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/tasks/update-deadline/{id}")
    public String updateDeadline(@PathVariable Long id,
                                 @RequestParam("deadline") String deadline) {
        taskService.getTaskById(id).ifPresent(task -> {
            task.setDeadline(LocalDate.parse(deadline));
            task.setStatus("In Progress");
            taskService.saveTask(task);
        });
        return "redirect:/admin/dashboard";
    }
}