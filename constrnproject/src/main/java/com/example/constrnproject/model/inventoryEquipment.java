package com.example.constrnproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="inventoryEquipment")
public class inventoryEquipment {

    private long equipmentId;
    private String equipmentName;

    @ManyToOne (fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private project Project;

    private String status;
    
    @ManyToOne (fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "taskId")
    private long assignedTaskId;

    public void setequipmentId(long equipmentId) {
        this.equipmentId = equipmentId;
    }
    public long getequipmentId() {
        return equipmentId;
    }
    public void setequipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }
    public String getequipmentName() {
        return equipmentName;
    }
    public void setproject(project Project) {
        this.Project = Project;
    }
    public project getproject() {
        return Project;
    }
    public void setstatus(String status) {
        this.status = status;
    }
    public String getstatus() {
        return status;
    }
    public void setassignedTaskId(long assignedTaskId) {
        this.assignedTaskId = assignedTaskId;
    }
    public long getassignedTaskId() {
        return assignedTaskId;
    }


}
