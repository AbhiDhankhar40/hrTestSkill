package com.test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.demo.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
