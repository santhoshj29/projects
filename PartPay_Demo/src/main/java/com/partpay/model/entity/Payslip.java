package com.partpay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

@Entity
@Table(name = "payslips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payslip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    @JsonIgnore
    private PartTimeEmployee employee;
    
    @Column(name = "generated_date", nullable = false)
    private LocalDate generatedDate;
    
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;
    
    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;
    
    @Column(name = "hours_worked", nullable = false)
    private Integer hoursWorked;
    
    @Column(name = "pay_per_hour", nullable = false)
    private Integer payPerHour;
    
    @Column(name = "gross_pay", nullable = false)
    private Integer grossPay;
    
    @Column(name = "net_pay", nullable = false)
    private Integer netPay;
    
    @Column(name = "tax_id", nullable = false)
    private Long taxId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id", insertable = false, updatable = false)
    @JsonIgnore
    private TaxInformation taxInformation;
    
    public Payslip(Long employeeId, LocalDate generatedDate, LocalDate fromDate, LocalDate toDate, 
                   Integer hoursWorked, Integer payPerHour, Integer grossPay, Integer netPay, Long taxId) {
        this.employeeId = employeeId;
        this.generatedDate = generatedDate;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.hoursWorked = hoursWorked;
        this.payPerHour = payPerHour;
        this.grossPay = grossPay;
        this.netPay = netPay;
        this.taxId = taxId;
    }
}