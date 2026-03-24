package com.test.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.demo.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByIsActiveTrue();

    List<Session> findByIsAdmissionSessionTrue();
}
