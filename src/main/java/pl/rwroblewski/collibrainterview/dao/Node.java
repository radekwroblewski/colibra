package pl.rwroblewski.collibrainterview.dao;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Node {
	private final String id;
	
	private final Map<Node, Set<Edge>> edgesFrom = new HashMap<>();
	
	private final Set<Node> nodesWithEdgesTo = new HashSet<>();

	public Node(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Map<Node, Set<Edge>> getEdgesFrom() {
		return edgesFrom;
	}

	public void addEdge(int weight, Node target) {
		Set<Edge> edgesToNode = getEdgesFrom().get(target);
		if (isNull(edgesToNode)) {
			edgesToNode = new TreeSet<>();
			getEdgesFrom().put(target, edgesToNode);
		}
		Edge edge = new Edge(weight, this, target);
		edgesToNode.add(edge);
		target.addNodeWithEdgeTo(this);
	}

	private void addNodeWithEdgeTo(Node node) {
		getNodesWithEdgesTo().add(node);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Node) && ((Node) obj).getId().equals(getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public void removeAllEdges() {
		getEdgesFrom().keySet().forEach(target -> target.getNodesWithEdgesTo().remove(this));
		getNodesWithEdgesTo().forEach(source -> source.getEdgesFrom().remove(this));
		getEdgesFrom().clear();
		getNodesWithEdgesTo().clear();
	}

	public void removeEdges(Node target) {
		getEdgesFrom().remove(target);
		target.removeEdgesFrom(this);
	}

	private void removeEdgesFrom(Node source) {
		getNodesWithEdgesTo().remove(source);
	}

	private Set<Node> getNodesWithEdgesTo() {
		return nodesWithEdgesTo;
	}

}
