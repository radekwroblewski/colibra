package pl.rwroblewski.collibrainterview.repository;

import java.time.Instant;

import org.springframework.web.socket.WebSocketSession;

public interface SessionRepository {

    String terminateSession(WebSocketSession session);

    void registerSession(WebSocketSession session);

    void registerCommunication(WebSocketSession session);

    Instant getLastCommunicationTime(WebSocketSession session);

    void setSessionName(WebSocketSession session, String name);

}
