package pl.rwroblewski.collibrainterview.repository;

import pl.rwroblewski.collibrainterview.dao.Node;

public interface NodeRepository {

	void addNode(String nodeName);

	void addEdge(String startNodeName, String endNodeName, int weight);

	void removeNode(String nodeName);

	void removeEdge(String startNodeName, String endNodeName);

	boolean containsNode(String nodeName);

	Node getNode(String nodeName);

}
