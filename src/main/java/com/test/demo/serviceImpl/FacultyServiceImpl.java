package com.test.demo.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.test.demo.model.Faculty;
import com.test.demo.repository.FacultyRepository;
import com.test.demo.service.FacultyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository repository;

    @Override
    public Faculty createFaculty(Faculty faculty) {
        return repository.save(faculty);
    }

    @Override
    public Faculty updateFaculty(Long id, Faculty faculty) {
        Faculty existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + id));

        existing.setFacultyName(faculty.getFacultyName());
        existing.setFacultyCode(faculty.getFacultyCode());
        existing.setFaculty(faculty.getFaculty());
        existing.setIsActive(faculty.getIsActive());
        existing.setCreatedBy(faculty.getCreatedBy());
        existing.setUpdatedBy(faculty.getUpdatedBy());

        return repository.save(existing);
    }

    @Override
    public Faculty getFacultyById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + id));
    }

    @Override
    public List<Faculty> getAllFaculties() {
        return repository.findAll();
    }

    @Override
    public void deleteFaculty(Long id) {
        repository.deleteById(id);
    }
}
