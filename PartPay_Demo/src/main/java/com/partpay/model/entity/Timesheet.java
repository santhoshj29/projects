package com.partpay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "timesheets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timesheet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    @JsonIgnore
    private PartTimeEmployee employee;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "actual_start_time", nullable = false)
    private LocalDateTime actualStartTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    public Timesheet(Long employeeId, LocalDate date, LocalDateTime actualStartTime, LocalDateTime actualEndTime) {
        this.employeeId = employeeId;
        this.date = date;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
    }
}