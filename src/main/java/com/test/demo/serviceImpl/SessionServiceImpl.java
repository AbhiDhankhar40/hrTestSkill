package com.test.demo.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.demo.model.Session;
import com.test.demo.repository.SessionRepository;
import com.test.demo.service.SessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repository;

    @Override
    public Session createSession(Session session) {
        if (Boolean.TRUE.equals(session.getIsActive())) {
            deactivateOtherActiveSessions(null);
        }

        if (Boolean.TRUE.equals(session.getIsAdmissionSession())) {
            deactivateOtherAdmissionSessions(null);
        }

        return repository.save(session);
    }

    @Override
    public Session updateSession(Long id, Session session) {
        Session existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        existing.setSession(session.getSession());
        existing.setStartDate(session.getStartDate());
        existing.setEndDate(session.getEndDate());
        existing.setIsActive(session.getIsActive());
        existing.setIsAdmissionSession(session.getIsAdmissionSession());

        if (Boolean.TRUE.equals(existing.getIsActive())) {
            deactivateOtherActiveSessions(id);
        }

        if (Boolean.TRUE.equals(existing.getIsAdmissionSession())) {
            deactivateOtherAdmissionSessions(id);
        }

        return repository.save(existing);
    }

    @Override
    public Session getSessionById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
    }

    @Override
    public List<Session> getAllSessions() {
        return repository.findAll();
    }

    @Override
    public void deleteSession(Long id) {
        repository.deleteById(id);
    }

    private void deactivateOtherActiveSessions(Long excludeSessionId) {
        List<Session> activeSessions = repository.findByIsActiveTrue();
        List<Session> toUpdate = new ArrayList<>();

        for (Session activeSession : activeSessions) {
            if (excludeSessionId == null || !activeSession.getSessionId().equals(excludeSessionId)) {
                activeSession.setIsActive(false);
                toUpdate.add(activeSession);
            }
        }

        if (!toUpdate.isEmpty()) {
            repository.saveAll(toUpdate);
        }
    }

    private void deactivateOtherAdmissionSessions(Long excludeSessionId) {
        List<Session> admissionSessions = repository.findByIsAdmissionSessionTrue();
        List<Session> toUpdate = new ArrayList<>();

        for (Session admissionSession : admissionSessions) {
            if (excludeSessionId == null || !admissionSession.getSessionId().equals(excludeSessionId)) {
                admissionSession.setIsAdmissionSession(false);
                toUpdate.add(admissionSession);
            }
        }

        if (!toUpdate.isEmpty()) {
            repository.saveAll(toUpdate);
        }
    }
}
