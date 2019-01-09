package pl.rwroblewski.collibrainterview.dao;

import static java.util.Objects.nonNull;

public class Edge implements Comparable<Edge> {
	private final Integer weight;
	
	private final Node start;
	
	public Node getStart() {
		return start;
	}

	public Node getEnd() {
		return end;
	}

	private final Node end;

	public Edge(int weight, Node start, Node end) {
		this.weight = weight;
		this.start = start;
		this.end = end;
	}
	
	public int getWeight() {
		return weight;
	}

	@Override
	public int compareTo(Edge o) {
		return nonNull(o) ? weight.compareTo(o.getWeight()) : 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	@Override
	public int hashCode() {
		return weight.hashCode();
	}

}
