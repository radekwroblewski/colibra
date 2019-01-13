package pl.rwroblewski.collibrainterview.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import pl.rwroblewski.collibrainterview.dao.Node;

@Component
public class NodeRepositoryImpl implements NodeRepository {
    private static final Map<String, Node> nodes = new HashMap<>();

    @Override
    public void addNode(String nodeName) {
        nodes.put(nodeName, new Node(nodeName));
    }

    @Override
    public void addEdge(String startNodeName, String endNodeName, int weight) {
        nodes.get(startNodeName).addEdge(weight, nodes.get(endNodeName));
    }

    @Override
    public void removeNode(String nodeName) {
        nodes.get(nodeName).removeAllEdges();
        nodes.remove(nodeName);
    }

    @Override
    public void removeEdge(String startNodeName, String endNodeName) {
        nodes.get(startNodeName).removeEdges(nodes.get(endNodeName));
    }

    @Override
    public boolean containsNode(String nodeName) {
        return nodes.containsKey(nodeName);
    }

    @Override
    public Node getNode(String nodeName) {
        return nodes.get(nodeName);
    }

}
