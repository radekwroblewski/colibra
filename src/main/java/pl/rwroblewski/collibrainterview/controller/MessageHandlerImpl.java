package pl.rwroblewski.collibrainterview.controller;

import static java.util.Objects.nonNull;

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

import pl.rwroblewski.collibrainterview.exception.ValidationException;
import pl.rwroblewski.collibrainterview.repository.SessionRepository;
import pl.rwroblewski.collibrainterview.service.NodeService;

@Component
public class MessageHandlerImpl implements MessageHandler {

    private static final String ALPHANUMERIC_STRING = "([a-zA-Z0-9/-]+)";

    private static final String NUMERIC_STRING = "([0-9]+)";

    private String DIDNT_UNDERSTAND = "SORRY, I DIDN'T UNDERSTAND THAT";

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private NodeService nodeService;

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
    public String handleMessage(WebSocketSession session, String message) throws ValidationException {
        Optional<? extends Entry<Matcher, Method>> handler = MESSAGE_HANDLERS.entrySet().stream()
                .map(entry -> new SimpleEntry<Matcher, Method>(entry.getKey().matcher(message), entry.getValue()))
                .filter(entry -> entry.getKey().matches()).findFirst();
        if (handler.isPresent()) {
            Method handlerMethod = handler.get().getValue();
            Matcher matcher = handler.get().getKey();
            Object[] params = new Object[matcher.groupCount() + 1];
            params[0] = session;
            for (int i = 1; i <= matcher.groupCount(); i++) {
                params[i] = matcher.group(i);
            }
            try {
                Object response = handlerMethod.invoke(this, params);
                return nonNull(response) ? response.toString() : null;
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

    @MessagePattern("ADD NODE " + ALPHANUMERIC_STRING)
    public String addNode(WebSocketSession session, String name) throws ValidationException {
        return nodeService.addNode(name);
    }

    @MessagePattern("ADD EDGE " + ALPHANUMERIC_STRING + " " + ALPHANUMERIC_STRING + " " + NUMERIC_STRING)
    public String addEdge(WebSocketSession session, String from, String to, String weight) throws ValidationException {
        return nodeService.addEdge(from, to, Integer.parseInt(weight));
    }

    @MessagePattern("REMOVE NODE " + ALPHANUMERIC_STRING)
    public String removeNode(WebSocketSession session, String name) throws ValidationException {
        return nodeService.removeNode(name);
    }

    @MessagePattern("REMOVE EDGE " + ALPHANUMERIC_STRING + " " + ALPHANUMERIC_STRING)
    public String removeEdge(WebSocketSession session, String from, String to)
            throws ValidationException {
        return nodeService.removeEdge(from, to);
    }

    @MessagePattern("SHORTEST PATH "+ALPHANUMERIC_STRING + " " + ALPHANUMERIC_STRING)
    public String shortestPath(WebSocketSession session, String from, String to) throws ValidationException {
        return nodeService.shortestPath(from, to);
    }
    
    @MessagePattern("CLOSER THAN "+NUMERIC_STRING + " " + ALPHANUMERIC_STRING)
    public String closerThan(WebSocketSession session, String weight, String from) throws ValidationException {
        return nodeService.closerThan(from, Integer.parseInt(weight));
    }
}
