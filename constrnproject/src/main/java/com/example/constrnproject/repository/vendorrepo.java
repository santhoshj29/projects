package com.example.constrnproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.constrnproject.model.vendor;

@Repository
public interface vendorrepo extends JpaRepository<vendor, Long> {

}
