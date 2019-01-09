package pl.rwroblewski.collibrainterview.service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.rwroblewski.collibrainterview.dao.Edge;
import pl.rwroblewski.collibrainterview.dao.Node;
import pl.rwroblewski.collibrainterview.exception.ValidationException;
import pl.rwroblewski.collibrainterview.repository.NodeRepository;

@Service
public class NodeServiceImpl implements NodeService {

	@Autowired
	private NodeRepository nodeRepository;

	@Autowired
	private ValidationErrorHandler errorHandler;

	@Autowired
	private NodeValidator nodeValidator;

	@Override
	public String addNode(String nodeName) {
		try {
			synchronized (nodeRepository) {
				nodeValidator.validateNotExists(nodeName);
				nodeRepository.addNode(nodeName);
			}
		} catch (ValidationException e) {
			return errorHandler.getErrorMessage(e);
		}
		return "NODE ADDED";
	}

	@Override
	public String addEdge(String startNodeName, String endNodeName, int weight) {
		try {
			synchronized (nodeRepository) {
				nodeValidator.validateExists(startNodeName);
				nodeValidator.validateExists(endNodeName);
				nodeRepository.addEdge(startNodeName, endNodeName, weight);
			}
		} catch (ValidationException e) {
			return errorHandler.getErrorMessage(e);
		}
		return "EDGE ADDED";
	}

	@Override
	public String removeNode(String nodeName) {
		try {
			synchronized (nodeRepository) {
				nodeValidator.validateExists(nodeName);
				nodeRepository.removeNode(nodeName);
			}
		} catch (ValidationException e) {
			return errorHandler.getErrorMessage(e);
		}
		return "NODE REMOVED";
	}

	@Override
	public String removeEdge(String startNodeName, String endNodeName) {
		try {
			synchronized (nodeRepository) {
				nodeValidator.validateExists(startNodeName);
				nodeValidator.validateExists(endNodeName);
				nodeRepository.removeEdge(startNodeName, endNodeName);
			}
		} catch (ValidationException e) {
			return errorHandler.getErrorMessage(e);
		}
		return "EDGE REMOVED";
	}

	@Override
	public String shortestPath(String startNodeName, String endNodeName) {
		try {
			synchronized (nodeRepository) {
				nodeValidator.validateExists(startNodeName);
				nodeValidator.validateExists(endNodeName);
				return String.format("%d", getShortestPath(startNodeName, endNodeName));
			}
		} catch (ValidationException e) {
			return errorHandler.getErrorMessage(e);
		}
	}

	@Override
	public String closerThan(String nodeName, int weight) {
		try {
			synchronized (nodeRepository) {
				nodeValidator.validateExists(nodeName);
				List<String> nodeNames = getNodesCloserThan(nodeRepository.getNode(nodeName), weight).stream()
						.map(Node::getId).sorted().collect(toList());
				return String.join(",", nodeNames);
			}
		} catch (ValidationException e) {
			return errorHandler.getErrorMessage(e);
		}
	}

	private Collection<Node> getNodesCloserThan(Node node, int weight) {
		Set<Node> nodes = findNodesCloserThan(node, weight, new HashSet<>());
		nodes.remove(node);
		return nodes;
	}

	private Set<Node> findNodesCloserThan(Node node, int weight, Set<Node> visitedNodes) {
		Set<Node> nodes = new HashSet<>();
		if (weight > 0) {
			nodes.add(node);
			visitedNodes.add(node);
			nodes.addAll(
				node.getEdgesFrom().entrySet().stream()
					.filter(entry -> !visitedNodes.contains(entry.getKey()) && !entry.getValue().isEmpty())
					.map(entry -> findNodesCloserThan(entry.getKey(), weight - getShortestEdgeWeight(entry), visitedNodes)
			).flatMap(Collection::stream).collect(toSet()));
		}
		return nodes;
	}

	private int getShortestEdgeWeight(Entry<Node, Set<Edge>> entry) {
		// assuming edges are sorted by weight.
		return entry.getValue().iterator().next().getWeight();
	}

	private int getShortestPath(String startNodeName, String endNodeName) {
		return getShortestPath(nodeRepository.getNode(startNodeName), nodeRepository.getNode(endNodeName), 0,
				Integer.MAX_VALUE, new HashSet<>());
	}

	private int getShortestPath(Node from, Node to, int currentPathLength, int currentShortestPathLength,
			Set<Node> visitedNodes) {
		if (from.equals(to)) {
			return Math.min(currentPathLength, currentShortestPathLength);
		}
		visitedNodes.add(from);
		int shortestPath = from.getEdgesFrom().entrySet().stream()
				.filter(entry -> !visitedNodes.contains(entry.getKey()) && !entry.getValue().isEmpty())
				.mapToInt(entry -> {
					return getShortestPath(entry.getKey(), to, currentPathLength + getShortestEdgeWeight(entry),
							currentShortestPathLength, visitedNodes);
				}).min().orElse(Integer.MAX_VALUE);
		return Math.min(currentShortestPathLength, shortestPath);
	}

}
