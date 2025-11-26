package com.example.constrnproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.constrnproject.model.Task;

@Repository
public interface Taskrepo extends JpaRepository<Task, Long> {

    // Custom query method to find tasks by project ID
    java.util.List<Task> findByProject_ProjectId(Long projectId);
}
