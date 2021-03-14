package cn.ecnu.tabusearch.utils;

import cn.ecnu.tabusearch.swaps.Gate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileResult {
    private List<List<Integer>> qlist=new ArrayList<>();
    private List<List<Integer>> lolist=new ArrayList<>();
    private long ngates=0;
    private Integer index=-1;
    private long n2gates=0;
    List<List<Gate>> layers = new ArrayList<>();

    public long getN2gates() {
        return n2gates;
    }

    public void setN2gates(long n2gates) {
        this.n2gates = n2gates;
    }

    public long getNgates() {
        return ngates;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setNgates(long ngates) {
        this.ngates = ngates;
    }

    public List<List<Integer>> getQlist() {
        return qlist;
    }

    public void setQlist(List<List<Integer>> qlist) {
        this.qlist = qlist;
    }

    public List<List<Integer>> getLolist() {
        return lolist;
    }

    public void setLolist(List<List<Integer>> lolist) {
        this.lolist = lolist;
    }

    public List<List<Gate>> getLayers() {
        return layers;
    }

    public void setLayers(List<List<Gate>> layers) {
        this.layers = layers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileResult that = (FileResult) o;
        return ngates == that.ngates &&
                Objects.equals(qlist, that.qlist) &&
                Objects.equals(lolist, that.lolist) &&
                Objects.equals(index, that.index) &&
                Objects.equals(n2gates, that.n2gates) &&
                Objects.equals(layers, that.layers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qlist, lolist, ngates, index, n2gates, layers);
    }

    @Override
    public String toString() {
        return "FileResult{" +
                "qlist=" + qlist +
                ", lolist=" + lolist +
                ", ngates=" + ngates +
                ", index=" + index +
                ", n2gates=" + n2gates +
                ", layers=" + layers +
                '}';
    }
}
