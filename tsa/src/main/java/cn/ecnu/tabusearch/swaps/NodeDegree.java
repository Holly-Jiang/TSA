package cn.ecnu.tabusearch.swaps;

import java.util.Objects;

public class NodeDegree implements Comparable{
    private  Integer nodeId;
    private Integer degree;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeDegree that = (NodeDegree) o;
        return Objects.equals(nodeId, that.nodeId) &&
                Objects.equals(degree, that.degree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, degree);
    }

    @Override
    public String toString() {
        return "NodeDegree{" +
                "nodeId=" + nodeId +
                ", degree=" + degree +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        NodeDegree o1=(NodeDegree) o;
        return this.degree-o1.getDegree();
    }
}
