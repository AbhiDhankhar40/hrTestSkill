package com.test.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.test.demo.model.AnswerEntry;

public interface AnswerEntryRepository extends JpaRepository<AnswerEntry, Long> {
    Optional<AnswerEntry> findByDataEntryId(Long dataEntryId);
}
