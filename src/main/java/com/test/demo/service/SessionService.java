package com.test.demo.service;

import java.util.List;

import com.test.demo.model.Session;

public interface SessionService {

    Session createSession(Session session);

    Session updateSession(Long id, Session session);

    Session getSessionById(Long id);

    List<Session> getAllSessions();

    void deleteSession(Long id);
}
