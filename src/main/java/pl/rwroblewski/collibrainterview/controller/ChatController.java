package pl.rwroblewski.collibrainterview.controller;

import java.util.logging.Logger;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

	private static Logger logger = Logger.getLogger("ChatController");

	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public String sendMessage(@Payload String message) {
		return message;
	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public String addUser(@Payload String chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session
		System.out.println("DUUUUUUUUUUPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA JAAAAAAAAAAAASIUUUUUUUUUUU");
		System.out.println(headerAccessor);
		logger.warning("DUUUUUUUUUUUUUUUUPA");
		headerAccessor.getSessionAttributes().entrySet().forEach(entry -> {
			logger.warning(entry.getKey());
			logger.warning(entry.getValue().toString());
		});
		headerAccessor.getSessionAttributes().put("username", "bob");
		return chatMessage;
	}

}