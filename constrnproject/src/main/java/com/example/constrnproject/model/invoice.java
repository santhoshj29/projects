package com.example.constrnproject.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="invoice")
public class invoice {

    private long invoiceId;

    @ManyToOne(fetch=jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private project Project;

    private String type; //CLIENT or VENDOR

    private double amount;

    private String status;

    private LocalDate dueDate;

    public void setinvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }
    public long getinvoiceId() {
        return invoiceId;
    }
    public void setproject(project Project) {
        this.Project = Project;
    }
    public project getproject() {
        return Project;
    }
    public void settype(String type) {
        this.type = type;
    }
    public String gettype() {
        return type;
    }
    public void setamount(double amount) {
        this.amount = amount;
    }
    public double getamount() {
        return amount;
    }
    public void setstatus(String status) {
        this.status = status;
    }
    public String getstatus() {
        return status;
    }
    public void setdueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public LocalDate getdueDate() {
        return dueDate;
    }


}
