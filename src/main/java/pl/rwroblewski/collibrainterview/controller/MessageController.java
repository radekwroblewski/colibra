package pl.rwroblewski.collibrainterview.controller;

import java.util.AbstractMap.SimpleEntry;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

	private static final String ALPHANUMERIC_STRING = "([a-zA-Z0-9/-]+)";

	private String DIDNT_UNDERSTAND = "SORRY, I DIDN'T UNDERSTAND THAT";

	private static Map<Pattern, Method> MESSAGE_HANDLERS = getControllerMethods(MessageController.class);
	

	public static Map<Pattern, Method> getControllerMethods(final Class<?> type) {
		final Map<Pattern, Method> methods = new HashMap<>();
		final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(type.getDeclaredMethods()));
		for (final Method method : allMethods) {
			if (method.isAnnotationPresent(MessagePattern.class)) {
				MessagePattern annotInstance = method.getAnnotation(MessagePattern.class);
				methods.put(Pattern.compile(String.format("^%s\n$", annotInstance.value())), method);
			}
		}
		return methods;
	}

	@MessageMapping("/")
	@SendTo("/")
	public String getMessage(@Payload String message) {
		Optional<? extends Entry<Matcher, Method>> handler = MESSAGE_HANDLERS.entrySet().stream()
			.map(entry -> new SimpleEntry<Matcher, Method>(entry.getKey().matcher(message), entry.getValue()))
			.filter(entry -> entry.getKey().matches()).findFirst();
		if (handler.isPresent()) {
			Method handlerMethod = handler.get().getValue();
			return handlerMethod.invoke(this, handler.get().getKey().group(1));
		}

		return DIDNT_UNDERSTAND;
	}

	@MessagePattern("HI, I'M " + ALPHANUMERIC_STRING)
	public String welcome(String name) {
		return String.format("HI %s", name);
	}
	
	@MessagePattern("BYE MATE!")
	public String goodbye() {
		return String.format("BYE %s, WE SPOKE FOR %d MS");
	}
	
	

}
