package com.test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.demo.model.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
}
