package com.example.constrnproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.constrnproject.model.notification;

@Repository
public interface notificationrepo extends JpaRepository<notification, Long> {

}
