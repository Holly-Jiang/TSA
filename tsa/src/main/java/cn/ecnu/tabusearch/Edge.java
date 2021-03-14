package cn.ecnu.tabusearch;

import java.util.Objects;

public class Edge implements Comparable{
    public Integer source = -1;    // the source / origin of the edge
    public Integer target = -1;    // the target / destination of the edge
    public Integer weight = 0;  //边的权重
    private Integer degree=0;

    public Edge(Edge e) {
        this.source=e.source;
        this.target=e.target;
        this.weight=e.weight;
        this.degree=e.degree;
    }

    public Edge() {
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source +
                ", target=" + target +
                ", weight=" + weight +
                ", degree=" + degree +
                '}';
    }
//A*
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Edge edge = (Edge) o;
//        return Objects.equals(source, edge.source) &&
//                Objects.equals(target, edge.target) &&
//                Objects.equals(weight, edge.weight) &&
//                Objects.equals(degree, edge.degree);
//    }

    //tabu
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(source, edge.source) &&
                Objects.equals(target, edge.target) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, weight, degree);
    }

    @Override
    public int compareTo(Object o) {
        Edge e=(Edge)o;
        int res=this.source +this.target- (e.source + e.target);
        if (res==0){
            int x=this.source-e.source;
            if (x==0){
                return this.target-e.target;
            }
            return x;
        }
        return res;
    }
}
