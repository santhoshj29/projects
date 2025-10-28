package com.partpay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parttimeemployee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartTimeEmployee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long uid;
    
    @Column(name = "pay_per_hour", nullable = false)
    private Double payPerHour;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "routing_number")
    private String routingNumber;
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<EmployeeSchedule> schedules = new HashSet<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Timesheet> timesheets = new HashSet<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<OvertimeRequest> overtimeRequests = new HashSet<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Payslip> payslips = new HashSet<>();
    
    public PartTimeEmployee(Long uid, Double payPerHour) {
        this.uid = uid;
        this.payPerHour = payPerHour;
    }
}