package pl.rwroblewski.collibrainterview.repository;

import static java.util.Objects.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SessionRepositoryImpl implements SessionRepository {

    private static final Map<WebSocketSession, SessionParams> SESSION_PARAMS = new HashMap<>();

    @Override
    public String terminateSession(WebSocketSession session) {
        synchronized (SESSION_PARAMS) {
            SessionParams params = SESSION_PARAMS.get(session);
            if (nonNull(params)) {
                long time = Duration.between(params.getStartTime(), Instant.now()).toMillis();
                String response = String.format("BYE %s, WE SPOKE FOR %d MS", params.getName(), time);
                SESSION_PARAMS.remove(session);
                return response;
            }
        }
        return "";
    }

    @Override
    public void setSessionName(WebSocketSession session, String name) {
        synchronized (SESSION_PARAMS) {
            SessionParams params = SESSION_PARAMS.get(session);
            if (nonNull(params)) {
                params.setName(name);
            }
        }
    }

    @Override
    public void registerSession(WebSocketSession session) {
        synchronized (SESSION_PARAMS) {
            SESSION_PARAMS.put(session, new SessionParams());
        }
    }

    @Override
    public void registerCommunication(WebSocketSession session) {
        synchronized (SESSION_PARAMS) {
            SessionParams params = SESSION_PARAMS.get(session);
            if (nonNull(params)) {
                params.setLastCommunicationTime();
            }
        }
    }

    @Override
    public Instant getLastCommunicationTime(WebSocketSession session) {
        synchronized (SESSION_PARAMS) {
            SessionParams params = SESSION_PARAMS.get(session);
            if (nonNull(params)) {
                return params.getLastCommunicationTime();
            }
        }
        return null;
    }

}

class SessionParams {
    private final Instant startTime = Instant.now();
    private Instant lastCommunicationTime = startTime;
    private String name = "";

    public Instant getLastCommunicationTime() {
        return lastCommunicationTime;
    }

    public void setLastCommunicationTime() {
        this.lastCommunicationTime = Instant.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartTime() {
        return startTime;
    }

}
