package com.example.constrnproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.constrnproject.model.inventoryMaterial;

@Repository
public interface inventoryMaterialrepo extends JpaRepository<inventoryMaterial, Long> {

}
