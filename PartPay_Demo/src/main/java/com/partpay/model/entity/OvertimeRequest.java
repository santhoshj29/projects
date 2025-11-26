package com.partpay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

@Entity
@Table(name = "overtimerequests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeRequest {
    
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
    
    @Column(nullable = false)
    private Integer hours;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    public OvertimeRequest(Long employeeId, LocalDate date, Integer hours, RequestStatus status) {
        this.employeeId = employeeId;
        this.date = date;
        this.hours = hours;
        this.status = status;
    }
    
    public enum RequestStatus {
        pending, approved, rejected
    }
}