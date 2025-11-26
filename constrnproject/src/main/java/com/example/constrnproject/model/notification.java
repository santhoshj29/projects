package com.example.constrnproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="notification")
public class notification {

    private long notificationId;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private project Project;

    private String message;

    private String type;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "employeeId")
    private humanresource recipientId;

    public void setnotificationId(long notificationId) {
        this.notificationId = notificationId;
    }
    public long getnotificationId() {
        return notificationId;
    }
    public void setproject(project Project) {
        this.Project = Project;
    }
    public project getproject() {
        return Project;
    }
    public void setmessage(String message) {
        this.message = message;
    }
    public String getmessage() {
        return message;
    }
    public void settype(String type) {
        this.type = type;
    }
    public String gettype() {
        return type;
    }
    public void setrecipientId(humanresource recipientId) {
        this.recipientId = recipientId;
    }
    public humanresource getrecipientId() {
        return recipientId;
    }
    

    

}
