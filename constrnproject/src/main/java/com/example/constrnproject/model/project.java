package com.example.constrnproject.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="project")
public class project {
    
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long projectId;
    
    private String projectName;
    
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal budget;

    private BigDecimal actualamount;

    private double completionPercentage;

    private String status;

    private long projectManagerId;

    private long clientId;

    public void setprojectId(long projectId) {
        this.projectId = projectId;
    }
    public long getprojectId() {
        return projectId;
    }
    public void setprojectName(String projectName) {
        this.projectName = projectName;
    }
    public String getprojectName() {
        return projectName;
    }
    public void setdescription(String description) {
        this.description = description;
    }
    public String getdescription() {
        return description;
    }
    public void setstartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getstartDate() {
        return startDate;
    }
    public void setendDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public LocalDate getendDate() {
        return endDate;
    }
    public void setbudget(BigDecimal budget) {
        this.budget = budget;
    }
    public BigDecimal getbudget() {
        return budget;
    }
    public void setactualamount(BigDecimal actualamount) {
        this.actualamount = actualamount;
    }
    public BigDecimal getactualamount() {
        return actualamount;
    }

    public void setcompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    public double getcompletionPercentage() {
        return completionPercentage;
    }
    

    public void setstatus(String status) {
        this.status = status;
    }
    public String getstatus() {
        return status;
    }
    public long getclientId() {
        return clientId;
    }
    public void setclientId(long clientId) {
        this.clientId = clientId;
    }
    public long getprojectManagerId() {
        return projectManagerId;
    }

    public void setprojectManagerId(long projectManagerId) {
        this.projectManagerId = projectManagerId;
    }

    

}
