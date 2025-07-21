package com.example.tmsproject.service;

import com.example.tmsproject.model.Task;
import com.example.tmsproject.model.User;
import com.example.tmsproject.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByAssignedToUsername(user.getUsername());
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}