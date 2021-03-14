package cn.ecnu.tabusearch.utils;

import cn.ecnu.tabusearch.Edge;
import cn.ecnu.tabusearch.exception.OvertimeException;
import cn.ecnu.tabusearch.swaps.Gate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CircuitsUtil {
    public static void buildUpCircuit(List<Gate> all_gates , List<Edge>  swaps, Set<Edge> graph
            ,List<List<Gate>> layers,List<Integer> locations,Integer i){

        List<Gate> h_gates = new ArrayList<>();
        if (i != 0) {
                for (Edge e : swaps) {
                    Gate cnot = new Gate();
                    Gate h1 = new Gate();
                    Gate h2 = new Gate();
                    if (GraphUtil.contains(graph, e)) {
                        cnot.setControl(e.source);
                        cnot.setTarget(e.target);
                    } else {
                        cnot.setControl(e.target);
                        cnot.setTarget(e.source);
                        int tmp = e.source;
                        e.source = e.target;
                        e.target = tmp;
                        if (!GraphUtil.contains(graph, e)) {
                            System.out.println("ERROR: invalid SWAP gate");
                            System.exit(-2);
                        }
                    }
                    cnot.setType("cx");
                    h1.setType("h");
                    h2.setType("h");
                    h1.setControl(-1);
                    h2.setControl(-1);
                    h1.setTarget(e.source);
                    h2.setTarget(e.target);
                    Gate gg = new Gate();
                    gg.setControl(cnot.getControl());
                    gg.setTarget(cnot.getTarget());
                    gg.setType("SWAP");
                    all_gates.add(cnot);
                    all_gates.add(h1);
                    all_gates.add(h2);
                    all_gates.add(cnot);
                    all_gates.add(h1);
                    all_gates.add(h2);
                    all_gates.add(cnot);
                    all_gates.add(gg);
                }

        }
        List<Gate> layer_vec = new ArrayList<>(layers.get(i));
        for (Gate g1 : layer_vec) {
            Gate gm = new Gate(g1);
            if (gm.getControl().equals(-1)) {
                if (locations.get(gm.getTarget()).equals(-1)) {
                    Gate g2 = new Gate();
                    g2.setControl(gm.getControl());
                    g2.setTarget(gm.getTarget());
                    g2.setType(gm.getType());
                    g2.setTarget(-gm.getTarget() - 1);
                    all_gates.add(g2);
                } else {
                    gm.setTarget(locations.get(gm.getTarget()));
                    all_gates.add(gm);
                }
            } else {
                gm.setTarget(locations.get(gm.getTarget()));
                gm.setControl(locations.get(gm.getControl()));
                Edge e = new Edge();
                e.source = gm.getControl();
                e.target = gm.getTarget();
                if (!GraphUtil.contains(graph, e)) {
                    e.source = gm.getTarget();
                    e.target = gm.getControl();
                    if (!GraphUtil.contains(graph, e)) {
                        System.out.println("ERROR: invalid CNOT: " + e.source + " - " + e.target);
                        System.exit(-3);
                    }
                    Gate h = new Gate();
                    h.setControl(-1);
                    h.setType("h");
                    h.setTarget(gm.getTarget());
                    all_gates.add(h);
                    h_gates.add(h);
                    h.setTarget(gm.getControl());
                    all_gates.add(h);
                    h_gates.add(h);
                    int tmp = gm.getTarget();
                    gm.setTarget(gm.getControl());
                    gm.setControl(tmp);
                }
                all_gates.add(gm);
            }
        }
        if (h_gates.size() != 0) {
            for (Gate it : h_gates) {
                all_gates.add(it);
            }
        }
    }
    public static void writeCiscuit(List<Gate> all_gates, List<Integer> qubits, List<Integer> locations,
                             Integer positions, String method, Integer nqubits,Integer total_swaps,String argv2) throws IOException {

//Fix the position of the single qubit gates
        for (Gate it : all_gates) {
            if (it.getType().equals("SWAP")) {
                int tmp_qubit1 = qubits.get(it.getControl());
                int tmp_qubit2 = qubits.get(it.getTarget());
                qubits.set(it.getControl(), tmp_qubit2);
                qubits.set(it.getTarget(), tmp_qubit1);
                if (tmp_qubit1 != -1) {
                    locations.set(tmp_qubit1, it.getTarget());
                }
                if (tmp_qubit2 != -1) {
                    locations.set(tmp_qubit2, it.getControl());
                }
            }
            if (it.getTarget() < 0) {
                int target = -(it.getTarget() + 1);
                it.setTarget(locations.get(target));
                if (locations.get(target).equals(-1)) {
                    //This qubit occurs only in single qubit gates -> it can be mapped to an arbirary physical qubit
                    int loc = 0;
                    while (qubits.get(loc) != -1) {
                        loc++;
                    }
                    locations.set(target, loc);
                }
            }
        }
        List<Integer> last_layer = new ArrayList<>();
        for (int i = 0; i < positions; i++) {
            last_layer.add(-1);
        }
        List<List<Gate>> mapped_circuit = new ArrayList<>();

        for (Gate g : all_gates) {
            if (g.getType().equals("SWAP")) {
                continue;
            }
            if (g.getControl() == -1) {
                int layer = last_layer.get(g.getTarget()) + 1;
                if (mapped_circuit.size() <= layer) {
                    mapped_circuit.add(new ArrayList<>());
                }
                mapped_circuit.get(layer).add(g);
                last_layer.set(g.getTarget(), layer);

            } else {
                int layer = Math.max(last_layer.get(g.getControl()), last_layer.get(g.getTarget())) + 1;
                if (mapped_circuit.size() <= layer) {
                    mapped_circuit.add(new ArrayList<>());
                }
                mapped_circuit.get(layer).add(g);
                last_layer.set(g.getTarget(), layer);
                last_layer.set(g.getControl(), layer);
            }
        }

        System.out.println(method + " After mapping (no post mapping optimizations are conducted): ");
        System.out.println("  elementary gates: " + (all_gates.size() - total_swaps));
        System.out.println("  depth: " + mapped_circuit.size());
        System.out.println("Initial mapping of the logical qubits (q) to the physical qubits (Q) of the IBM QX3 architecture: ");

        for (int i = 0; i < nqubits; i++) {
            System.out.println("  q" + i + " is initially mapped to Q" + locations.get(i));
        }

        System.out.println(method + ": " + (all_gates.size() - total_swaps) + " " + mapped_circuit.size() + " " + total_swaps);
        PrintWriter pw=new PrintWriter("E:\\github\\quantum_compiler_optim\\result_java\\"+argv2+"_Zulehner");
        pw.println("OPENQASM 2.0;");
        pw.println("include \"qelib1.inc\";");
        pw.println("qreg q[20];");
        pw.println("creg c[20];");
        for (List<Gate> it:mapped_circuit){
            for (Gate it2:it){
                pw.print(it2.getType()+" ");
                if (!it2.getControl().equals(-1)){
                    pw.print("q["+it2.getControl()+ "],");
                }
                pw.println("q["+it2.getTarget()+ "];");
            }
        }
        pw.close();

    }
}
