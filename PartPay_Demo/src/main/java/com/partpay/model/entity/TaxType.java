package com.partpay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

// ========== TaxType Entity ==========
@Entity
@Table(name = "taxtypes")
@Data
@NoArgsConstructor
@AllArgsConstructor
class TaxType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "deduction_percentage", nullable = false)
    private Integer deductionPercentage;
    
    @OneToMany(mappedBy = "taxType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<TaxTaxType> taxTaxTypes = new HashSet<>();
    
    public TaxType(String name, Integer deductionPercentage) {
        this.name = name;
        this.deductionPercentage = deductionPercentage;
    }
}