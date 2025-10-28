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
@Table(name = "taxtaxtype")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TaxTaxTypeId.class)
class TaxTaxType {
    
    @Id
    @Column(name = "tax_information_id")
    private Long taxInformationId;
    
    @Id
    @Column(name = "tax_type_id")
    private Long taxTypeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_information_id", insertable = false, updatable = false)
    @JsonIgnore
    private TaxInformation taxInformation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_type_id", insertable = false, updatable = false)
    @JsonIgnore
    private TaxType taxType;
    
    public TaxTaxType(Long taxInformationId, Long taxTypeId) {
        this.taxInformationId = taxInformationId;
        this.taxTypeId = taxTypeId;
    }
}

// ========== Composite Key for TaxTaxType ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
class TaxTaxTypeId implements Serializable {
    private Long taxInformationId;
    private Long taxTypeId;
}