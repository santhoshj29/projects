package com.example.constrnproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.constrnproject.model.budgets;

@Repository
public interface budgetsrepo extends JpaRepository<budgets, Long> {

}
