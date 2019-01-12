package pl.rwroblewski.collibrainterview.controller;

import org.springframework.web.socket.WebSocketSession;

import pl.rwroblewski.collibrainterview.exception.ValidationException;

public interface MessageHandler {

    String handleMessage(WebSocketSession session, String message) throws ValidationException;

}
