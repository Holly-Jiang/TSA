package cn.ecnu.tabusearch.swaps;

import cn.ecnu.tabusearch.Edge;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node implements Comparable {
	
	private int id; // a unique id - running number
	private   int cost_fixed;
	private int cost_heur;
	private int cost_heur2;
	private int depth;
	private List<Integer> qubits; // get qubit of location -> -1 indicates that there is "no" qubit at a certain location
	private List<Integer> locations; // get location of qubits -> -1 indicates that a qubit does not have a location -> shall only occur for i > nqubits
	private int nswaps;
	private int done;
	private List<List<Edge>> swaps;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCost_fixed() {
		return cost_fixed;
	}

	public void setCost_fixed(int cost_fixed) {
		this.cost_fixed = cost_fixed;
	}

	public int getCost_heur() {
		return cost_heur;
	}

	public void setCost_heur(int cost_heur) {
		this.cost_heur = cost_heur;
	}

	public int getCost_heur2() {
		return cost_heur2;
	}

	public void setCost_heur2(int cost_heur2) {
		this.cost_heur2 = cost_heur2;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public List<Integer> getQubits() {
		return qubits;
	}

	public void setQubits(List<Integer> qubits) {
		this.qubits = qubits;
	}

	public List<Integer> getLocations() {
		return locations;
	}

	public void setLocations(List<Integer> locations) {
		this.locations = locations;
	}

	public int getNswaps() {
		return nswaps;
	}

	public void setNswaps(int nswaps) {
		this.nswaps = nswaps;
	}

	public int getDone() {
		return done;
	}

	public void setDone(int done) {
		this.done = done;
	}

	public List<List<Edge>> getSwaps() {
		return swaps;
	}

	public void setSwaps(List<List<Edge>> swaps) {
		this.swaps = swaps;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		return id == node.id &&
				cost_fixed == node.cost_fixed &&
				cost_heur == node.cost_heur &&
				cost_heur2 == node.cost_heur2 &&
				depth == node.depth &&
				nswaps == node.nswaps &&
				done == node.done &&
				Objects.equals(qubits, node.qubits) &&
				Objects.equals(locations, node.locations) &&
				Objects.equals(swaps, node.swaps);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, cost_fixed, cost_heur, cost_heur2, depth, qubits, locations, nswaps, done, swaps);
	}

	@Override
	public String toString() {
		return "Node{" +
				"id=" + id +
				", cost_fixed=" + cost_fixed +
				", cost_heur=" + cost_heur +
				", cost_heur2=" + cost_heur2 +
				", depth=" + depth +
				", qubits=" + qubits +
				", locations=" + locations +
				", nswaps=" + nswaps +
				", done=" + done +
				", swaps=" + swaps +
				'}';
	}

	@Override
	public int compareTo(Object o) {
		Node y=(Node)o;
		if ((this.getCost_fixed() + this.getCost_heur() + this.getCost_heur2())
				!= (y.getCost_fixed() + y.getCost_heur() + y.getCost_heur2())) {
			return ((this.getCost_fixed() + this.getCost_heur() + this.getCost_heur2()))-((y.getCost_fixed() + y.getCost_heur() + y.getCost_heur2()));
		}
		if (this.getDone() == 1) {
			return 1;
		}
		if (y.getDone() == 1) {
			return -1;
		}
		return (this.getCost_heur() + this.getCost_heur2()) -(y.getCost_heur() + y.getCost_heur2());
	}
}