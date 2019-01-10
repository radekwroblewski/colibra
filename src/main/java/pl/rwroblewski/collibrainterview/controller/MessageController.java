package pl.rwroblewski.collibrainterview.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
	
	private String DIDNT_UNDERSTAND = "SORRY, I DIDN'T UNDERSTAND THAT";
	
	static {
		Reflectio
	}
	
	@MessageMapping("/")
	@SendTo("/")
	public String getMessage(@Payload String message) {
		
		
		return DIDNT_UNDERSTAND;
	}

}
