package com.example.constrnproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="inventory")
public class inventoryMaterial {

    private long materialId;
    private String materialName;
    
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private project Project;

    private int quantity;

    private String unit;

    private long vendorId;

    public void setmaterialId(long materialId) {
        this.materialId = materialId;
    }
    public long getmaterialId() {
        return materialId;
    }
    public void setmaterialName(String materialName) {
        this.materialName = materialName;
    }
    public String getmaterialName() {
        return materialName;
    }
    public void setprojectId(project Project) {
        this.Project = Project;
    }
    public project getproject() {
        return Project;
    }
    public void setquantity(int quantity) {
        this.quantity = quantity;
    }
    public int getquantity() {
        return quantity;
    }
    public void setunit(String unit) {
        this.unit = unit;
    }
    public String getunit() {
        return unit;
    }
    public void setvendorId(long vendorId) {
        this.vendorId = vendorId;
    }
    public long getvendorId() {
        return vendorId;
    }

    
}
