package com.example.constrnproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.constrnproject.model.invoice;

@Repository
public interface invoicerepo extends JpaRepository<invoice, Long> {

}
