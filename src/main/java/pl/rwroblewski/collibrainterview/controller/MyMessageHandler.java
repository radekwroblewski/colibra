package pl.rwroblewski.collibrainterview.controller;

import org.springframework.web.socket.WebSocketSession;

public interface MyMessageHandler {

    String handleMessage(WebSocketSession session, String message);

}
