package pl.rwroblewski.collibrainterview;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import pl.rwroblewski.collibrainterview.controller.MessageHandler;
import pl.rwroblewski.collibrainterview.exception.ValidationException;
import pl.rwroblewski.collibrainterview.repository.SessionRepository;
import pl.rwroblewski.collibrainterview.service.ValidationErrorHandler;

@Component
public class SocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = Logger.getLogger(SocketHandler.class.getName());

    private static final long TIMEOUT = 30000;

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ValidationErrorHandler errorHandler;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        sessionRepository.registerCommunication(session);
        startSessionTimer(session);
        String response;
        try {
            response = messageHandler.handleMessage(session, message.getPayload());
        } catch (ValidationException e) {
            response = errorHandler.getErrorMessage(e);
        }
        try {
            sendMessage(session, response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus arg1) throws Exception {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UUID randomUUID = UUID.randomUUID();

        synchronized (sessionRepository) {
            sessionRepository.registerSession(session);
            sendMessage(session, String.format("HI, I'M %s", randomUUID.toString()));
            startSessionTimer(session);
        }
    }

    private void startSessionTimer(WebSocketSession session) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TIMEOUT);
                    synchronized (sessionRepository) {
                        Instant lastCommunicationTime = sessionRepository.getLastCommunicationTime(session);
                        if (isNull(lastCommunicationTime)
                                || lastCommunicationTime.isBefore(Instant.now().minusMillis(TIMEOUT))) {
                            sendMessage(session, sessionRepository.terminateSession(session));
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void sendMessage(WebSocketSession session, String message) throws IOException {
        LOGGER.info(String.format("Sending to: '%s'; message: '%s'", session.getId(), message));
        session.sendMessage(new TextMessage(message + "\n"));
    }
}