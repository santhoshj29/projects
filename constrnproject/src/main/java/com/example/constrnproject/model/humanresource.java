package com.example.constrnproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="humanresource")
public class humanresource {

    private long employeeId;
    private String employeeName;
    private String role;

    private String skills;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private project Project;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "taskId")
    private Task task;

    public void setemployeeId(long employeeId) {
        this.employeeId = employeeId;
    }
    public long getemployeeId() {
        return employeeId;
    }
    public void setemployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    public String getemployeeName() {
        return employeeName;
    }
    public void setrole(String role) {
        this.role = role;
    }
    public String getrole() {
        return role;
    }
    public void setskills(String skills) {
        this.skills = skills;
    }

    public String getskills() {
        return skills;
    }
    public void setproject(project Project) {
        this.Project = Project;
    }
    public project getprojectId() {
        return Project;
    }
    public void setTask(Task task) {
        this.task = task;
    }
    public Task getTask() {
        return task;
    }

}
