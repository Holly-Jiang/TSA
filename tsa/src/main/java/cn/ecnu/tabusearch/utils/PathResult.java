package cn.ecnu.tabusearch.utils;

import cn.ecnu.tabusearch.Edge;

import java.util.*;

public class PathResult {
    private Set<Edge> graph=new TreeSet<>();
    private Integer [] degrees;

    public Set<Edge> getGraph() {
        return graph;
    }

    public void setGraph(Set<Edge> graph) {
        this.graph = graph;
    }

    public Integer[] getDegrees() {
        return degrees;
    }

    public void setDegrees(Integer[] degrees) {
        this.degrees = degrees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathResult that = (PathResult) o;
        return Objects.equals(graph, that.graph) &&
                Arrays.equals(degrees, that.degrees);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(graph);
        result = 31 * result + Arrays.hashCode(degrees);
        return result;
    }

    @Override
    public String toString() {
        return "PathResult{" +
                "graph=" + graph +
                ", degrees=" + Arrays.toString(degrees) +
                '}';
    }
}
