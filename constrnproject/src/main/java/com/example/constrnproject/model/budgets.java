package com.example.constrnproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="budgets")  
public class budgets {

    
    private long budgetId;
    
    @ManyToOne(fetch=jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private project Project;

    private double plannedbudget;
    private double actualbudget;

    private double variance;

    public void setbudgetId(long budgetId) {
        this.budgetId = budgetId;
    }
    public long getbudgetId() {
        return budgetId;
    }
    public void setproject(project Project) {
        this.Project = Project;
    }
    public project getproject() {
        return Project;
    }
    public void setplannedbudget(double plannedbudget) {
        this.plannedbudget = plannedbudget;
    }
    public double getplannedbudget() {
        return plannedbudget;
    }
    public void setactualbudget(double actualbudget) {
        this.actualbudget = actualbudget;
    }
    public double getactualbudget() {
        return actualbudget;
    }
    public void setvariance(double variance) {
        this.variance = variance;
    }
    public double getvariance() {
        return variance;
    }


}
