package pl.rwroblewski.collibrainterview.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import pl.rwroblewski.collibrainterview.repository.SessionRepository;

@Component
public class MessageHandlerImpl implements MyMessageHandler {

    private static final String ALPHANUMERIC_STRING = "([a-zA-Z0-9/-]+)";

    private String DIDNT_UNDERSTAND = "SORRY, I DIDN'T UNDERSTAND THAT";

    @Autowired
    private SessionRepository sessionRepository;

    private static Map<Pattern, Method> MESSAGE_HANDLERS = getControllerMethods(MessageHandlerImpl.class);

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

    @Override
    public String handleMessage(WebSocketSession session, String message) {
        Optional<? extends Entry<Matcher, Method>> handler = MESSAGE_HANDLERS.entrySet().stream()
                .map(entry -> new SimpleEntry<Matcher, Method>(entry.getKey().matcher(message), entry.getValue()))
                .filter(entry -> entry.getKey().matches()).findFirst();
        if (handler.isPresent()) {
            Method handlerMethod = handler.get().getValue();
            Matcher matcher = handler.get().getKey();
            Object[] params = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                params[i] = matcher.group(i + 1);
            }
            try {
                return handlerMethod.invoke(this, params).toString();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return DIDNT_UNDERSTAND;
    }

    @MessagePattern("HI, I'M " + ALPHANUMERIC_STRING)
    public String welcome(WebSocketSession session, String name) {
        sessionRepository.setSessionName(session, name);
        return String.format("HI %s", name);
    }

    @MessagePattern("BYE MATE!")
    public String goodbye(WebSocketSession session) {
        return sessionRepository.terminateSession(session);
    }

}
