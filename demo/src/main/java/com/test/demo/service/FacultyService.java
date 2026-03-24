package com.test.demo.service;

import java.util.List;

import com.test.demo.model.Faculty;

public interface FacultyService {

    Faculty createFaculty(Faculty faculty);

    Faculty updateFaculty(Long id, Faculty faculty);

    Faculty getFacultyById(Long id);

    List<Faculty> getAllFaculties();

    void deleteFaculty(Long id);
}
