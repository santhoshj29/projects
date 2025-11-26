package com.partpay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "swaprequests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwapRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "shiftid", nullable = false)
    private Long shiftId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shiftid", insertable = false, updatable = false)
    @JsonIgnore
    private EmployeeSchedule shift;
    
    @Column(name = "targetshiftid", nullable = false)
    private Long targetShiftId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetshiftid", insertable = false, updatable = false)
    @JsonIgnore
    private EmployeeSchedule targetShift;
    
    @Column
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    public SwapRequest(Long shiftId, Long targetShiftId, String reason, RequestStatus status) {
        this.shiftId = shiftId;
        this.targetShiftId = targetShiftId;
        this.reason = reason;
        this.status = status;
    }
    
    public enum RequestStatus {
        pending, approved, rejected
    }
}