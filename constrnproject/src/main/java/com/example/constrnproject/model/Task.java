package com.example.constrnproject.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="task")
public class Task {
    
    @Id
    private long taskId;
    private String taskName;
    
    @ManyToOne (fetch = FetchType.LAZY)  // Many tasks belong to one project
    @JoinColumn(name = "projectId") // foreign key in task table
    private project Project;

    private String taskDescription;
    private String assignedTo;
    private LocalDate startDate;
    private LocalDate dueDate;
    private double completionPercentage;
    private String status;
    
    @Column(name = "dependencies")
    // ✅ Use ElementCollection for List<Long>
    @ElementCollection
    @CollectionTable(name = "task_dependencies", joinColumns = @JoinColumn(name = "taskId"))
    //@Column(name = "dependency_task_id")
    private final List<Long> dependencies = new ArrayList<>(); // List of task IDs this task depends on


    public void addDependecy(Long taskId){
        if(!dependencies.contains(taskId)){
            dependencies.add(taskId);
        }
    }
    public List<Long> getDependencies(){
        return List.copyOf(dependencies);
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
    public long getTaskId() {
        return taskId;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getTaskName() {
        return taskName;
    }
    
    public void setProject(project Project) {
        this.Project = Project;
    }
    public project getProject() {
        return Project;
    }
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    public String getTaskDescription() {
        return taskDescription;
    }
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    public String getAssignedTo() {
        return assignedTo;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    public double getCompletionPercentage() {
        return completionPercentage;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }


}



/* 
 
✅ Option 1 — JPA Way (Recommended)

You fetch or create the Project entity and assign it directly:

Project project = projectRepository.findById("AB1").orElseThrow();
Task task = new Task();
task.setTaskId(101);
task.setTaskName("Foundation Work");
task.setProject(project); // <--- this links via projectId automatically
taskRepository.save(task);

*/