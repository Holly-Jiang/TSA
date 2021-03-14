package cn.ecnu.tabusearch.utils;

import cn.ecnu.tabusearch.Edge;

import java.util.Iterator;
import java.util.Set;

public class GraphUtil {
    public static Boolean  contains(Set<Edge> graph, Edge comp){
        for(Edge e: graph){
            if ((e.source==comp.source&&e.target==comp.target)){
                return true;
            }
        }
        return false;
}

}
