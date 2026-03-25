package com.test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.test.demo.model.Options;

@Repository
public interface OptionsRepository extends JpaRepository<Options, Long> {
}