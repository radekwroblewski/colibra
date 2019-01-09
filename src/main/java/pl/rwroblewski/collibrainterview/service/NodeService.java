package pl.rwroblewski.collibrainterview.service;

public interface NodeService {
	String addNode(String nodeName);

	String addEdge(String startNodeName, String endNodeName, int weight);

	String removeNode(String nodeName);

	String removeEdge(String startNodeName, String endNodeName);

	String shortestPath(String startNodeName, String endNodeName);

	String closerThan(String nodeName, int weight);

}
