package com.example.constrnproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.constrnproject.model.inventoryEquipment;

@Repository
public interface inventoryEquipmentrepo extends JpaRepository<inventoryEquipment, Long> {

}
