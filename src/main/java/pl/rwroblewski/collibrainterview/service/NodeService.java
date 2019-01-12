package pl.rwroblewski.collibrainterview.service;

import pl.rwroblewski.collibrainterview.exception.ValidationException;

public interface NodeService {
    String addNode(String nodeName) throws ValidationException;

    String addEdge(String startNodeName, String endNodeName, int weight) throws ValidationException;

    String removeNode(String nodeName) throws ValidationException;

    String removeEdge(String startNodeName, String endNodeName) throws ValidationException;

    String shortestPath(String startNodeName, String endNodeName) throws ValidationException;

    String closerThan(String nodeName, int weight) throws ValidationException;

}
