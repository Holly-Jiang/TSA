package cn.ecnu.tabusearch.swaps;

import cn.ecnu.tabusearch.Edge;
import cn.ecnu.tabusearch.ShortPath;
import cn.ecnu.tabusearch.exception.OvertimeException;
import cn.ecnu.tabusearch.run.main_Zulehner;
import cn.ecnu.tabusearch.utils.DateUtil;
import cn.ecnu.tabusearch.utils.PathResult;

import java.util.*;

public class AStarFixlayer {
    public static Integer positions = 20;

    public static Node aStarFixLayer(int layer, List<Integer> map, List<Integer> loc, ShortPath[][] dist,
                                     List<List<Gate>> layers, PathResult pathResult,Date start) throws OvertimeException {

        int next_layer = getNextLayer(layer, layers);
        Set<Edge> graph = pathResult.getGraph();
        Integer[] degrees = pathResult.getDegrees();
        PriorityQueue<Node> nodes = new PriorityQueue<>();
        Node n = new Node();
        n.setCost_fixed(0);
        n.setCost_heur(0);
        n.setCost_heur2(0);
        List<Integer> qu = new ArrayList<>();
        List<Integer> lo = new ArrayList<>();
        n.setQubits(qu);
        n.setLocations(lo);
        List<List<Edge>> sw = new ArrayList<List<Edge>>();
        n.setSwaps(sw);
        n.setDone(1);
        List<Gate> v = new ArrayList<>(layers.get(layer));
        List<Integer> considered_qubits = new ArrayList<>();
        for (Gate g : v) {
            if (g.getControl() != -1) {
                considered_qubits.add(g.getControl());
                considered_qubits.add(g.getTarget());
                if (loc.get(g.getControl()).equals(-1) && loc.get(g.getTarget()).equals(-1)) {
                    //候选边排序
                    Set<Edge> possible_edge = new TreeSet<>();
//                            new Comparator<Edge>() {
//                        @Override
//                        public int compare(Edge edge, Edge t1) {
//                            return degrees[edge.source] + degrees[edge.target] - (degrees[t1.source] + degrees[t1.target]);
//                        }
//                        @Override
//                        public int compare(Edge edge, Edge t1) {
//                            return edge.source +edge.target- (t1.source + t1.target);
//                        }
//                    }
                    Iterator<Edge> git = graph.iterator();
                    while (git.hasNext()) {
                        Edge e = git.next();
                        if (map.get(e.source).equals(-1) && map.get(e.target).equals(-1)) {
//                           if (possible_edge.contains(e)){
//                               System.out.println(e);
//                           }
                            possible_edge.add(e);
                        }
                    }
                    if (!possible_edge.isEmpty()) {
                        Iterator<Edge> pit = possible_edge.iterator();
                        Edge e = pit.next();
                        loc.set(g.getControl(), e.source);
                        map.set(e.source, g.getControl());
                        loc.set(g.getTarget(), e.target);
                        map.set(e.target, g.getTarget());
                    } else {
                        System.out.println("no edge available!");
                        System.exit(-1);
                    }

                } else if (loc.get(g.getControl()).equals(-1)) {
                    int min = 1000;
                    int min_pos = -1;
                    for (int i = 0; i < positions; i++) {
                        if (map.get(i).equals(-1) && dist[i][loc.get(g.getTarget())].getDistance() < min) {
                            min = dist[i][loc.get(g.getTarget())].getDistance();
                            min_pos = i;
                        }
                    }
                    map.set(min_pos, g.getControl());
                    loc.set(g.getControl(), min_pos);
                } else if (loc.get(g.getTarget()).equals(-1)) {
                    int min = 1000;
                    int min_pos = -1;
                    for (int i = 0; i < positions; i++) {
                        if (map.get(i).equals(-1) && dist[i][loc.get(g.getControl())].getDistance() < min) {
                            min = dist[i][loc.get(g.getControl())].getDistance();
                            min_pos = i;
                        }
                    }
                    map.set(min_pos, g.getTarget());
                    loc.set(g.getTarget(), min_pos);
                }
                n.setCost_heur(Math.max(n.getCost_heur(), dist[loc.get(g.getControl())][loc.get(g.getTarget())].getDistance()));
            } else {

            }
        }
        if (n.getCost_heur() > 4) {
            n.setDone(0);
        }
//        main_Zulehner.printList(loc);
        List<Integer> quu = new ArrayList<Integer>(map);
        n.setQubits(quu);
        List<Integer> locc = new ArrayList<Integer>(loc);
        n.setLocations(locc);
        nodes.add(n);
        List<Integer> used = new ArrayList<>();
        for (int i = 0; i < positions; i++) {
            used.add(0);
        }
        List<Edge> edges = new ArrayList<>();

        for (int i = 0; i < considered_qubits.size(); i++) {
            edges.add(new Edge());
        }
        while (nodes.peek().getDone() != 1) {
            Date end = new Date(System.currentTimeMillis());
            if (DateUtil.TimeDifference(start, end) > 300) {
                throw new OvertimeException("超时了 " + DateUtil.TimeDifference(start, end));
            }
            Node n1 = nodes.poll();
            expandNode(considered_qubits, 0, edges, 0, used, n1, v, dist, next_layer, layers, nodes, graph, start);
//            System.out.println(nodes.size());
        }
        Node result = nodes.poll();
        while (!nodes.isEmpty()) {
            Node n2 = nodes.poll();
        }
        return result;
    }

    private static void expandNode(List<Integer> qubits, int qubit, List<Edge> swaps, int nswaps,
                                   List<Integer> used, Node base_node, List<Gate> gates, ShortPath[][] dist,
                                   int next_layer, List<List<Gate>> layers, PriorityQueue<Node> nodes,
                                   Set<Edge> graph, Date start) throws OvertimeException {
        Date end = new Date(System.currentTimeMillis());
        if (DateUtil.TimeDifference(start, end) > 300) {
            throw new OvertimeException("超时了 " + DateUtil.TimeDifference(start, end));
        }
        if (qubit == qubits.size()) {
            if (nswaps == 0) {
                return;
            }

            Node newNode = new Node();
            List<Integer> qu = new ArrayList<>();
            List<Integer> lo = new ArrayList<>();
            for (int i = 0; i < base_node.getQubits().size(); i++) {
                qu.add(base_node.getQubits().get(i));
                lo.add(base_node.getLocations().get(i));
            }
            newNode.setQubits(qu);
            newNode.setLocations(lo);
            List<List<Edge>> sw = new ArrayList<>();
            newNode.setSwaps(sw);
            newNode.setNswaps(base_node.getNswaps() + nswaps);
            for (int i = 0; i < base_node.getSwaps().size(); i++) {
                List<Edge> new_v = new ArrayList<>(base_node.getSwaps().get(i));
                newNode.getSwaps().add(new_v);
            }
            newNode.setDepth(base_node.getDepth() + 5);
            newNode.setCost_fixed(base_node.getCost_fixed() + 7 * nswaps);
            newNode.setCost_heur(0);
            List<Edge> newSwaps = new ArrayList<>();
            for (int i = 0; i < nswaps; i++) {
                if (DateUtil.TimeDifference(start, end) > 300) {
                    throw new OvertimeException("超时了 " + DateUtil.TimeDifference(start, end));
                }
                newSwaps.add(new Edge(swaps.get(i)));
                int tmp_qubit1 = newNode.getQubits().get(swaps.get(i).source);
                int tmp_qubit2 = newNode.getQubits().get(swaps.get(i).target);

                newNode.getQubits().set(swaps.get(i).source, tmp_qubit2);
                newNode.getQubits().set(swaps.get(i).target, tmp_qubit1);
                if (tmp_qubit1 != -1) {
                    newNode.getLocations().set(tmp_qubit1, swaps.get(i).target);
                }
                if (tmp_qubit2 != -1) {
                    newNode.getLocations().set(tmp_qubit2, swaps.get(i).source);
                }
            }
            newNode.getSwaps().add(newSwaps);
            newNode.setDone(1);
            for (Gate g : gates) {
                if (DateUtil.TimeDifference(start, end) > 300) {
                throw new OvertimeException("超时了 " + DateUtil.TimeDifference(start, end));
            }
                if (g.getControl() != -1) {
                    newNode.setCost_heur(newNode.getCost_heur()
                            + dist[newNode.getLocations().get(g.getControl())][newNode.getLocations().get(g.getTarget())].getDistance());
                    if (dist[newNode.getLocations().get(g.getControl())][newNode.getLocations().get(g.getTarget())].getDistance() > 4) {
                        newNode.setDone(0);
                    }
                }
            }
            newNode.setCost_heur2(0);
            if (next_layer != -1) {
                for (Gate g : layers.get(next_layer)) {
                    if (DateUtil.TimeDifference(start, end) > 300) {
                        throw new OvertimeException("超时了 " + DateUtil.TimeDifference(start, end));
                    }
                    if (g.getControl() != -1) {
                        if (newNode.getLocations().get(g.getControl()).equals(-1) && newNode.getLocations().get(g.getTarget()).equals(-1)) {

                        } else if (newNode.getLocations().get(g.getControl()).equals(-1)) {
                            int min = 1000;
                            for (int i = 0; i < positions; i++) {
                                if (newNode.getQubits().get(i).equals(-1) && dist[i][newNode.getLocations().get(g.getTarget())].getDistance() < min) {
                                    min = dist[i][newNode.getLocations().get(g.getTarget())].getDistance();
                                }
                            }
                            newNode.setCost_heur2(newNode.getCost_heur2() + min);
                        } else if (newNode.getLocations().get(g.getTarget()).equals(-1)) {
                            int min = 1000;
                            for (int i = 0; i < positions; i++) {
                                if (newNode.getQubits().get(i).equals(-1) && dist[newNode.getLocations().get(g.getControl())][i].getDistance() < min) {
                                    min = dist[newNode.getLocations().get(g.getControl())][i].getDistance();
                                }
                            }
                            newNode.setCost_heur2(newNode.getCost_heur2() + min);
                        } else {
                            newNode.setCost_heur2(newNode.getCost_heur2() +
                                    dist[newNode.getLocations().get(g.getControl())][newNode.getLocations().get(g.getTarget())].getDistance());
                        }
                    }
                }
            }
            nodes.add(newNode);
        } else {
            if (DateUtil.TimeDifference(start, end) > 300) {
                throw new OvertimeException("超时了 " + DateUtil.TimeDifference(start, end));
            }
            expandNode(qubits, qubit + 1, swaps, nswaps, used, base_node, gates, dist, next_layer, layers, nodes, graph,start);
            Iterator<Edge> it = graph.iterator();
            while (it.hasNext()) {
                if (DateUtil.TimeDifference(start, end) > 300) {
                    throw new OvertimeException("超时了 " + DateUtil.TimeDifference(start, end));
                }
                Edge e = it.next();
                if (e.source == base_node.getLocations().get(qubits.get(qubit)) ||
                        e.target == base_node.getLocations().get(qubits.get(qubit))) {
                    if (used.get(e.source) == 0 && used.get(e.target) == 0) {
                        used.set(e.source, 1);
                        used.set(e.target, 1);
                        swaps.get(nswaps).source = e.source;
                        swaps.get(nswaps).target = e.target;
                        expandNode(qubits, qubit + 1, swaps, nswaps + 1, used, base_node, gates, dist, next_layer, layers, nodes, graph,start);
                        used.set(e.source, 0);
                        used.set(e.target, 0);
                    }
                }
            }
        }
    }

    private static int getNextLayer(int layer, List<List<Gate>> layers) {
        int next_layer = layer + 1;
        while (next_layer < layers.size()) {
            for (Gate g : layers.get(next_layer)) {
                if (g.getControl() != -1) {
                    return next_layer;
                }
            }
            next_layer++;
        }
        return -1;
    }
}
