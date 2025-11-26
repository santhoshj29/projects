package com.partpay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "user_orgs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserOrganizationId.class)
public class UserOrganization {
    
    @Id
    @Column(name = "org_id")
    private Long orgId;
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", insertable = false, updatable = false)
    private Organization organization;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @Column(nullable = false)
    private String role;
    
    public UserOrganization(Long orgId, Long userId, String role) {
        this.orgId = orgId;
        this.userId = userId;
        this.role = role;
    }
}

// Composite Key Class
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserOrganizationId implements Serializable {
    private Long orgId;
    private Long userId;
}