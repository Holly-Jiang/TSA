package cn.ecnu.tabusearch.utils;

import java.util.Objects;

public class NodeUtil {
    private  Integer id=0;
    private Integer degree=0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        NodeUtil nodeUtil = (NodeUtil) o;
        return Objects.equals(id, nodeUtil.id) &&
                Objects.equals(degree, nodeUtil.degree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, degree);
    }

    @Override
    public String toString() {
        return "NodeUtil{" +
                "id=" + id +
                ", degree=" + degree +
                '}';
    }
}
