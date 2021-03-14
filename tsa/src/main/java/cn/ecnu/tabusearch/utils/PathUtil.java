package cn.ecnu.tabusearch.utils;

import cn.ecnu.tabusearch.Edge;
import cn.ecnu.tabusearch.ShortPath;

import java.util.*;

public class PathUtil {
    private static Integer positions = 20;

    public static ShortPath bfs(Integer start, Integer goal, Set<Edge> graph) {
        ShortPath result = new ShortPath();
        result.setDistance(0);
        List<List<Integer>> solutions = new ArrayList<>();
        List<List<Edge>> edges=new ArrayList<>();
        result.setPaths(edges);
        Queue<List<Integer>> queue = new LinkedList<>();
        List<Integer> v = new ArrayList<>();
        v.add(start);
        queue.add(v);
        int length = 0;
        Set<Integer> successors = new HashSet<>();
        while (!queue.isEmpty()) {
            v = queue.peek();
            queue.poll();
            Integer current = v.get(v.size() - 1);
            if (current == goal) {
                length = v.size();
                solutions.add(v);
                break;
            } else {
                successors.clear();
                Iterator<Edge> it = graph.iterator();
                while (it.hasNext()) {
                    Edge e = it.next();
                    if (e.source == current && !v.contains(e.target)) {
                        successors.add(e.target);
                    }
                    if (e.target == current && !v.contains(e.source)) {
                        successors.add(e.source);
                    }
                }
                Iterator<Integer> succ = successors.iterator();
                while (succ.hasNext()) {
                    List<Integer> v2 = new ArrayList<>(v);
                    v2.add(succ.next());
                    queue.add(v2);
                }

            }

        }
        while (!queue.isEmpty() && queue.peek().size() == length) {
            if (queue.peek().get(queue.peek().size() - 1) == goal) {
                solutions.add(queue.peek());
            }
            queue.poll();
        }
        for (int i = 0; i < solutions.size(); i++) {
            List<Integer> v1 = solutions.get(i);
            List<Edge> e1=new ArrayList<>();
            for (int j = 0; j < v1.size() - 1; j++) {
                Edge e = new Edge();
                e.source = v1.get(j);
                e.target = v.get(j + 1);
                e1.add(e);
                if (GraphUtil.contains(graph, e)) {
                    result.setDistance((length - 2) * 7);
                    return result;
                }
            }
            edges.add(e1);
        }
        result.setDistance((length - 2) * 7 + 4);
        return result;
    }
    public static ShortPath[][] build_dist_table_tabu(Set<Edge> graph) {
        Integer positions = 20;
        ShortPath[][] dist = new ShortPath[positions][positions];
        for (int i = 0; i < positions; i++) {
            dist[i] = new ShortPath[positions];
        }

        for (int i = 0; i < positions; i++) {
            for (int j = 0; j < positions; j++) {
                if (i != j) {
//                    dist[i][j] = bfs(i, j, graph);
                    dist[i][j] = bfsForTabu(i, j, graph);
                } else {
                    dist[i][i] = new ShortPath();
                }
            }
        }
        return dist;
    }
    public static ShortPath[][] build_dist_table(Set<Edge> graph) {
        Integer positions = 20;
        ShortPath[][] dist = new ShortPath[positions][positions];
        for (int i = 0; i < positions; i++) {
            dist[i] = new ShortPath[positions];
        }

        for (int i = 0; i < positions; i++) {
            for (int j = 0; j < positions; j++) {
                if (i != j) {
                    dist[i][j] = bfs(i, j, graph);
                } else {
                    dist[i][i] = new ShortPath();
                }
            }
        }
        return dist;
    }
    public static ShortPath bfsForTabu(Integer start, Integer goal, Set<Edge> graph) {
        ShortPath result = new ShortPath();
//        List<Integer> distance=new ArrayList<>();
        List<List<Integer>> solutions = new ArrayList<>();
        List<List<Edge>> edges=new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();
        List<Integer> v = new ArrayList<>();
        v.add(start);
        queue.add(v);
        int length = 0;
        Set<Integer> successors = new HashSet<>();
        while (!queue.isEmpty()) {
            v = queue.peek();
            queue.poll();
            Integer current = v.get(v.size() - 1);
            if (current == goal) {
                length = v.size();
                solutions.add(v);
                break;
            } else {
                successors.clear();
                Iterator<Edge> it = graph.iterator();
                while (it.hasNext()) {
                    Edge e = it.next();
                    if (e.source == current && !v.contains(e.target)) {
                        successors.add(e.target);
                    }
                    if (e.target == current && !v.contains(e.source)) {
                        successors.add(e.source);
                    }
                }
                Iterator<Integer> succ = successors.iterator();
                while (succ.hasNext()) {
                    List<Integer> v2 = new ArrayList<>(v);
                    v2.add(succ.next());
                    queue.add(v2);
                }

            }

        }
        while (!queue.isEmpty() && queue.peek().size() == length) {
            if (queue.peek().get(queue.peek().size() - 1) == goal) {
                solutions.add(queue.peek());
            }
            queue.poll();
        }
//        Integer  flag=0;
        for (int i = 0; i < solutions.size(); i++) {
            List<Integer> v1 = solutions.get(i);
            List<Edge> e1=new ArrayList<>();
            for (int j = 0; j < v1.size() - 1; j++) {
                Edge e = new Edge();
                e.source = v1.get(j);
                e.target = v1.get(j + 1);
                e1.add(e);
//                if (!GraphUtil.contains(graph, e)) {
//                    Edge e2 = new Edge();
//                    e2.source=v.get(j + 1);
//                    e2.target=v1.get(j);
                    //需要反向操作
//                    if (GraphUtil.contains(graph, e2)){
//                        flag++;
//                    }else{
//                        // j j+1之间没有边
//                    }
//                }
            }
            edges.add(e1);
//            distance.add((length-1)*7);
        }
        //求最短距离花费，有些距离相等但是权重不相等，需要反向
//        Integer minDistance=99999999;
//        for (int i=0;i<distance.size();i++){
//            if (minDistance>distance.get(i)){
//                minDistance=distance.get(0);
//            }
//        }
//        for (int i=0;i<edges.size();i++){
//            if (minDistance<distance.get(i)){
//                distance.remove(i);
//                edges.remove(i);
//            }
//        }
        result.setDistance((length-1)*3);
        result.setPaths(edges);
        return result;
    }
    public static PathResult build_graph_QX20() {
        PathResult result = new PathResult();
        Integer [] degree=new Integer[positions];
        for (int i=0;i<positions;i++){
            degree[i]=0;
        }
        result.setDegrees(degree);
        Set<Edge> graph = new TreeSet<Edge>();
        result.setGraph(graph);
        graph.clear();
        positions = 20;
        Edge e1;
        Edge e2;
        for (int i = 0; i < 4; i++) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 1;
            e2.source = i + 1;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }
        for (int i = 5; i < 9; i++) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 1;
            e2.source = i + 1;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }
        for (int i = 10; i < 14; i++) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 1;
            e2.source = i + 1;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }

        for (int i = 15; i < 19; i++) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 1;
            e2.source = i + 1;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }
        for (int i = 0; i < 15; i++) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 5;
            e2.source = i + 5;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }
        for (int i = 1; i <= 7; i += 2) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 6;
            e2.source = i + 6;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }
        for (int i = 11; i <= 13; i += 2) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 6;
            e2.source = i + 6;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }
        for (int i = 2; i <= 8; i += 2) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 4;
            e2.source = i + 4;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }
        for (int i = 12; i <= 14; i += 2) {
            e1 = new Edge();
            e2 = new Edge();
            e1.source = i;
            e1.target = i + 4;
            e2.source = i + 4;
            e2.target = i;
            degree[i]++;
            degree[i+1]++;
            graph.add(e1);
            graph.add(e2);
        }
        return result;
    }
    public static PathResult build_graph_QX3() {
        PathResult result = new PathResult();
        positions = 16;
        Integer [] degree=new Integer[positions];
        for (int i=0;i<positions;i++){
            degree[i]=0;
        }
        result.setDegrees(degree);
        Set<Edge> graph = new TreeSet<Edge>();
        result.setGraph(graph);
        graph.clear();
        Edge e=new Edge();
        e.source = 0;
        e.target = 1;
        degree[0]++;
        degree[1]++;
        graph.add(e);
        e=new Edge();
        e.source = 1;
        e.target = 2;
        degree[2]++;
        degree[1]++;
        graph.add(e);
        e=new Edge();
        e.source = 2;
        e.target = 3;
        degree[2]++;
        degree[3]++;
        graph.add(e);
        e=new Edge();
        e.source = 3;
        e.target = 14;
        degree[3]++;
        degree[14]++;
        graph.add(e);
        e=new Edge();
        e.source = 4;
        e.target = 3;
        degree[4]++;
        degree[3]++;
        graph.add(e);
        e=new Edge();
        e.source = 4;
        e.target = 5;
        degree[4]++;
        degree[5]++;
        graph.add(e);
        e=new Edge();
        e.source = 6;
        e.target = 7;
        degree[6]++;
        degree[7]++;
        graph.add(e);
        e=new Edge();
        e.source = 6;
        e.target = 11;
        degree[6]++;
        degree[11]++;
        graph.add(e);
        e=new Edge();
        e.source = 7;
        e.target = 10;
        degree[7]++;
        degree[10]++;
        graph.add(e);
        e=new Edge();
        e.source = 8;
        e.target = 7;
        degree[8]++;
        degree[7]++;
        graph.add(e);
        e=new Edge();
        e.source = 9;
        e.target = 8;
        degree[9]++;
        degree[8]++;
        graph.add(e);
        e=new Edge();
        e.source = 9;
        e.target = 10;
        degree[9]++;
        degree[10]++;
        graph.add(e);
        e=new Edge();
        e.source = 11;
        e.target = 10;
        degree[11]++;
        degree[10]++;
        graph.add(e);
        e=new Edge();
        e.source = 12;
        e.target = 5;
        degree[12]++;
        degree[5]++;
        graph.add(e);
        e=new Edge();
        e.source = 12;
        e.target = 11;
        degree[12]++;
        degree[11]++;
        graph.add(e);
        e=new Edge();
        e.source = 12;
        e.target = 13;
        degree[12]++;
        degree[13]++;
        graph.add(e);
        e=new Edge();
        e.source = 13;
        e.target = 4;
        degree[13]++;
        degree[4]++;
        graph.add(e);
        e=new Edge();
        e.source = 13;
        e.target = 14;
        degree[13]++;
        degree[14]++;
        graph.add(e);
        e=new Edge();
        e.source = 15;
        e.target = 0;
        degree[15]++;
        degree[0]++;
        graph.add(e);
        e=new Edge();
        e.source = 15;
        e.target = 14;
        degree[15]++;
        degree[14]++;
        graph.add(e);
        return result;
    }
}
