package cn.ecnu.tabusearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShortPath {
    private List<List<Edge>> paths = new ArrayList<>();
    private Integer distance = 0;

    public List<List<Edge>> getPaths() {
        return paths;
    }

    public void setPaths(List<List<Edge>> paths) {
        this.paths = paths;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortPath shortPath = (ShortPath) o;
        return Objects.equals(paths, shortPath.paths) &&
                Objects.equals(distance, shortPath.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paths, distance);
    }

    @Override
    public String toString() {
        return "ShortPath{" +
                "paths=" + paths +
                ", distance=" + distance +
                '}';
    }
}
