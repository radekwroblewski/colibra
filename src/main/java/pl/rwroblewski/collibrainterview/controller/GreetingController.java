package pl.rwroblewski.collibrainterview.controller;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String greeting(String message) throws Exception {
		Thread.sleep(1000); // simulated delay
		return "Hello, " + HtmlUtils.htmlEscape(message) + "!";
	}

	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public String send(Message<String> message) throws Exception {
		return "GO AWAY";
	}

}