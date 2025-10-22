package com.example.constrnproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="vendor")
public class vendor {

    private long vendorId;
    private String vendorName;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private project Project;

    private String contactInfo;
    private String materialSupplied;

    public void setvendorId(long vendorId) {
        this.vendorId = vendorId;
    }
    public long getvendorId() {
        return vendorId;
    }
    public void setvendorName(String vendorName) {
        this.vendorName = vendorName;
    }
    public String getvendorName() {
        return vendorName;
    }
    public void setproject(project Project) {
        this.Project = Project;
    }
    public project getproject() {
        return Project;
    }
    public void setcontactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    public String getcontactInfo() {
        return contactInfo;
    }
    public void setmaterialSupplied(String materialSupplied) {
        this.materialSupplied = materialSupplied;
    }
    public String getmaterialSupplied() {
        return materialSupplied;
    }
    
}
