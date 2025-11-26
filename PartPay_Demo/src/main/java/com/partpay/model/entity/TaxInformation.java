package com.partpay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "taxinformation")
@Data
@NoArgsConstructor
@AllArgsConstructor
class TaxInformation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "taxInformation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<TaxTaxType> taxTaxTypes = new HashSet<>();
    
    @OneToMany(mappedBy = "taxInformation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Payslip> payslips = new HashSet<>();
    
    public TaxInformation(String name) {
        this.name = name;
    }
}
