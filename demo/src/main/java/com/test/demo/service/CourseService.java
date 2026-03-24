package com.test.demo.service;

import java.util.List;

import com.test.demo.model.Course;

public interface CourseService {

    Course createCourse(Course course);

    Course updateCourse(Long id, Course course);

    Course getCourseById(Long id);

    List<Course> getAllCourses();

    void deleteCourse(Long id);
}
