package com.test.demo.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.test.demo.model.Course;
import com.test.demo.repository.CourseRepository;
import com.test.demo.service.CourseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;

    @Override
    public Course createCourse(Course course) {
        return repository.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course course) {
        Course existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        existing.setCourseName(course.getCourseName());
        existing.setCourseCode(course.getCourseCode());
        existing.setCourseShortName(course.getCourseShortName());
        existing.setDuration(course.getDuration());
        existing.setCourseType(course.getCourseType());
        existing.setFacultyId(course.getFacultyId());
        existing.setCreatedBy(course.getCreatedBy());
        existing.setModifyBy(course.getModifyBy());
        existing.setIsActive(course.getIsActive());
        existing.setIsProspectusSale(course.getIsProspectusSale());
        existing.setTotalSeats(course.getTotalSeats());

        return repository.save(existing);
    }

    @Override
    public Course getCourseById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    @Override
    public List<Course> getAllCourses() {
        return repository.findAll();
    }

    @Override
    public void deleteCourse(Long id) {
        repository.deleteById(id);
    }
}
