package com.example.constrnproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.constrnproject.model.Task;
import com.example.constrnproject.repository.Taskrepo;

@Service
public class Taskservice {

    private final Taskrepo taskrepo;
    
    @Autowired
    public Taskservice(Taskrepo taskrepo) {
        this.taskrepo = taskrepo;
    }

    public Task createTask(Task task){
        taskrepo.save(task);
        return task;
    }

    public Task findByTaskId(Long taskId){

        return taskrepo.findById(taskId).orElse(null);

    }
    

}
