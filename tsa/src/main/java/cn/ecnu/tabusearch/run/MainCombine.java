package cn.ecnu.tabusearch.run;

import cn.ecnu.tabusearch.Edge;
import cn.ecnu.tabusearch.ShortPath;
import cn.ecnu.tabusearch.exception.OvertimeException;
import cn.ecnu.tabusearch.swaps.AStarFixlayer;
import cn.ecnu.tabusearch.swaps.Gate;
import cn.ecnu.tabusearch.swaps.Node;
import cn.ecnu.tabusearch.utils.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainCombine {

    private static Integer nqubits = 20;
    private static Integer positions = 20;
/*
Compare the initial mapping of wghtgraph, greedy, CSIC
 */
    public static void main(String[] args) throws IOException {
        if (args.length<2){
            System.out.println("usage: java -jar tabusearch.jar [small/medium/large/all] [connect/degree]");
            return ;
        }
        String filePath = "../../../src/main/resources/examples_result/";

        List<String> files = FileUtil.getFiles(filePath);
        /**
         * /home/test/qubitmapping
         *\\src\\main\\resources
         */
        String GQL_iniPath = "../../../src/main/resources/ini_mapping_q20/";
        String FY_iniPath = "../../../src/main/resources/testIni/";
        String out1 = "../../../src/main/resources/compare/total1_A_ini_connect" ;
        String out2 = "../../../src/main/resources/compare/total_A_ini_connect";
        FileWriter of1 = new FileWriter(out1, true);
        FileWriter of2 = new FileWriter(out2, true);
        for (int k = 0; k < files.size(); k++) {
            StringBuilder str = new StringBuilder(files.get(k));
            str.delete(0, str.lastIndexOf("/") + 1);
            String ss = str.substring(0, str.lastIndexOf("."));
            System.out.println(k+ " 处理文件： " + ss);
            String argv1 = files.get(k);
            String argv2 = ss;
            ss=argv2;
            Path qasm = Paths.get(argv1);
            FileResult fileResult = FileUtil.read_qasm(qasm, argv2);
            if (args[0].equals("small")){
                if (fileResult.getNgates()>=100){
                    continue;
                }
            }else  if (args[0].equals("medium")){
                if (fileResult.getNgates()<100||fileResult.getNgates()>1000){
                    continue;
                }
            }else if (args[0].equals("large")){
                if (fileResult.getNgates()<=1000){
                    continue;
                }
            }

            PathResult pathResult = PathUtil.build_graph_QX20();
            Set<Edge> graph = pathResult.getGraph();
            ShortPath[][] dist = PathUtil.build_dist_table(graph);
            // greedy
            getMainZulehnerResult(argv1, ss, "main_Zulehner", of1, of2, pathResult, dist, fileResult);
            //wghtgraph
            getMain_FYResult(argv1, ss, FY_iniPath, "main_FY", of1, of2, pathResult, dist, fileResult);
            //CSIC
            getMain_GQLResult1(argv1, ss, GQL_iniPath, "main_GQL", of1, of2, pathResult, dist, fileResult);
        }
        of1.close();
        of2.close();
    }

    public static void getMainZulehnerResult(String argv1, String argv2, String method, FileWriter of1, FileWriter of2,
                                             PathResult pathResult, ShortPath[][] dist, FileResult fileResult) throws IOException {
        Set<Edge> graph = pathResult.getGraph();
        List<List<Gate>> layers = fileResult.getLayers();

        long ngates = fileResult.getNgates();
        int width = 0;
        for (List<Gate> it : layers) {
            if (it.size() > width) {
                width = it.size();
            }
        }
        of1.append(argv2);
        of1.append("\n");

        of2.append(argv2);
        of2.append("\n");
        of2.append("initial: " + ngates + " " + layers.size() + " " + 0);
        of2.append("\n");
        of1.append(method + "_main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
        of1.append("\n");
        of1.append("Before mapping: ");
        of1.append("\n");
        of1.append("  elementary gates: " + ngates);
        of1.append("\n");
        of1.append("  depth: " + layers.size());
        of1.append("\n");
        System.out.println(method + "_main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
        of1.append("\n");
        System.out.println("Before mapping: ");
        of1.append("\n");
        System.out.println("  elementary gates: " + ngates);
        of1.append("\n");
        System.out.println("  depth: " + layers.size());
        of1.append("\n");
        List<Integer> locations = new ArrayList<>();
        List<Integer> qubits = new ArrayList<>();

        for (int i = 0; i < positions; i++) {
            qubits.add(-1);
            locations.add(-1);
        }

        for (int i = 0; i < nqubits; i++) {
            locations.set(i, i);
            qubits.set(i, i);
        }
        //Initially, no physical qubit is occupied
        for (int i = 0; i < positions; i++) {
            qubits.set(i, -1);
        }
        //Initially, no logical qubit is occupied
        for (int i = 0; i < nqubits; i++) {
            locations.set(i, -1);
        }
        int swap_count = 0;
        List<Gate> all_gates = new ArrayList<>();
        int total_swaps = 0;
        try {

            Date start = new Date(System.currentTimeMillis());
            for (int i = 0; i < layers.size(); i++) {
//                printList(locations);
                Node result = AStarFixlayer.aStarFixLayer(i, qubits, locations, dist, layers, pathResult,start);
                locations = result.getLocations();
                qubits = result.getQubits();
//                printList(locations);

//                List<Gate> h_gates = new ArrayList<>();
                if (i != 0) {
                    for (List<Edge> it : result.getSwaps()) {
                        for (Edge e : it) {
                            Gate cnot = new Gate();
                            cnot.setControl(e.source);
                            cnot.setTarget(e.target);
                            Gate cnot1 = new Gate();
                            cnot1.setControl(e.target);
                            cnot1.setTarget(e.source);
//                            Gate h1 = new Gate();
//                            Gate h2 = new Gate();
//                            if (GraphUtil.contains(graph, e)) {
//                                cnot.setControl(e.source);
//                                cnot.setTarget(e.target);
//                            } else {
//                                cnot.setControl(e.target);
//                                cnot.setTarget(e.source);
//                                int tmp = e.source;
//                                e.source = e.target;
//                                e.target = tmp;
//                                if (!GraphUtil.contains(graph, e)) {
//                                    System.out.println("ERROR: invalid SWAP gate");
//                                    System.exit(-2);
//                                }
//                            }
                            cnot.setType("cx");
                            cnot1.setType("cx");
//                            h1.setType("h");
//                            h2.setType("h");
//                            h1.setControl(-1);
//                            h2.setControl(-1);
//                            h1.setTarget(e.source);
//                            h2.setTarget(e.target);
                            Gate gg = new Gate();
                            gg.setControl(cnot.getControl());
                            gg.setTarget(cnot.getTarget());
                            gg.setType("SWAP");
                            swap_count += 1;
                            all_gates.add(cnot);
//                            all_gates.add(h1);
//                            all_gates.add(h2);
                            all_gates.add(cnot1);
//                            all_gates.add(h1);
//                            all_gates.add(h2);
                            all_gates.add(cnot);
                            all_gates.add(gg);
                            total_swaps++;
                        }
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
//                            Gate h = new Gate();
//                            h.setControl(-1);
//                            h.setType("h");
//                            h.setTarget(gm.getTarget());
//                            all_gates.add(h);
//                            h_gates.add(h);
//                            h.setTarget(gm.getControl());
//                            all_gates.add(h);
//                            h_gates.add(h);
                            int tmp = gm.getTarget();
                            gm.setTarget(gm.getControl());
                            gm.setControl(tmp);
                        }
                        all_gates.add(gm);
                    }
                }
//                if (h_gates.size() != 0) {
//                    if (result.getCost_heur() == 0) {
//                        System.out.println("ERROR: invalid heuristic cost!");
//                        System.exit(-2);
//                    }
//
//                    for (Gate it : h_gates) {
//                        all_gates.add(it);
//                    }
//                }
            }
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
            of2.append(method+": " + (all_gates.size() - total_swaps) + " " + mapped_circuit.size() + " " + total_swaps);
            of2.append("\n");
            of1.append(method + " After mapping (no post mapping optimizations are conducted): ");
            of1.append("\n");
            of1.append("  elementary gates: " + (all_gates.size() - total_swaps));
            of1.append("\n");
            of1.append("  depth: " + mapped_circuit.size());
            of1.append("\n");
            System.out.println(method + " After mapping (no post mapping optimizations are conducted): ");
            System.out.println("  elementary gates: " + (all_gates.size() - total_swaps));
            System.out.println("  depth: " + mapped_circuit.size());
//        of1.append( ) + "The mapping required " + time + " seconds" );

            of1.append("Initial mapping of the logical qubits (q) to the physical qubits (Q) of the IBM QX3 architecture: ");
            of1.append("\n");

//        cout ) + "The mapping required " + time + " seconds" );

            System.out.println("Initial mapping of the logical qubits (q) to the physical qubits (Q) of the IBM QX3 architecture: ");

            for (int i = 0; i < nqubits; i++) {
                of1.append("  q" + i + " is initially mapped to Q" + locations.get(i));
                of1.append("\n");
                System.out.println("  q" + i + " is initially mapped to Q" + locations.get(i));
            }

            System.out.println(method + ": " + (all_gates.size() - total_swaps) + " " + mapped_circuit.size() + " " + total_swaps);
            PrintWriter pw=new PrintWriter("../../../src/main/resources/ini_circuits_result/"+argv2+"_Zulehner");
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

        } catch (OutOfMemoryError e){
                of2.append(method+": " + 99999999 + " " + 99999999 + " " + 99999999);
                of2.append("\n");
                of1.append(method+": " + 99999999 + " " + 99999999 + " " + 99999999);
                of1.append("\n");
                System.out.println(method+" :" + 99999999 + " " + 99999999 + " " + 99999999);
            System.out.println(method+" Java heap space。。。。");
            e.printStackTrace();
         return ;
        }catch (OvertimeException e) {
            of2.append(method+": " + 99999999 + " " + 99999999 + " " + 99999999);
            of2.append("\n");
            of1.append(method+": " + 99999999 + " " + 99999999 + " " + 99999999);
            of1.append("\n");
            e.printStackTrace();
            System.out.println(method + " 超时了");
            return;
        } finally {
            of1.flush();
            of2.flush();
            System.gc();
        }
    }

    public static void getMain_FYResult(String argv1, String argv2, String iniPath, String method, FileWriter of1, FileWriter of2, PathResult pathResult,
                                        ShortPath[][] dist, FileResult fileResult) throws IOException {
        Set<Edge> graph = pathResult.getGraph();
        List<List<Gate>> layers = fileResult.getLayers();

        long ngates = fileResult.getNgates();
        int width = 0;
        for (List<Gate> it : layers) {
            if (it.size() > width) {
                width = it.size();
            }
        }
        of1.append(argv2);
        of1.append("\n");
        of1.append(method + "_main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
        of1.append("\n");
        of1.append("Before mapping: ");
        of1.append("\n");
        of1.append("  elementary gates: " + ngates);
        of1.append("\n");
        of1.append("  depth: " + layers.size());
        of1.append("\n");
        System.out.println(method + "_main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
        of1.append("\n");
        System.out.println("Before mapping: ");
        of1.append("\n");
        System.out.println("  elementary gates: " + ngates);
        of1.append("\n");
        System.out.println("  depth: " + layers.size());
        of1.append("\n");
        List<Integer> locations = new ArrayList<>();
        List<Integer> qubits = new ArrayList<>();

        for (int i = 0; i < positions; i++) {
            qubits.add(-1);
            locations.add(-1);
        }

        for (int i = 0; i < nqubits; i++) {
            locations.set(i, i);
            qubits.set(i, i);
        }
        //Initially, no physical qubit is occupied
        for (int i = 0; i < positions; i++) {
            qubits.set(i, -1);
        }
        //Initially, no logical qubit is occupied
        for (int i = 0; i < nqubits; i++) {
            locations.set(i, -1);
        }
        List<Gate> all_gates = new ArrayList<>();
        Integer min_depth = 9999999;
        Integer min_swap_nums = 9999999;
        Integer min_gate_nums = 9999999;
        Integer index = -1;
        Path mappingPath = Paths.get(iniPath, argv2 + ".qasm.txt");
        FileResult ini_mapping = FileUtil.readFYIni(mappingPath);
        if (ini_mapping.getLolist() == null || ini_mapping.getLolist().size() <= 0) {
            printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
            return;
        }
        try {
            //初始映射
            for (int x = 0; x < 1; x++) {
                for (int z = 0; z < ini_mapping.getLolist().get(x).size(); z++) {
                    locations.set(z, ini_mapping.getLolist().get(x).get(z));
                    qubits.set(z, ini_mapping.getQlist().get(x).get(z));
                }

                int total_swaps = 0;
                Date start = new Date(System.currentTimeMillis());
                for (int i = 0; i < layers.size(); i++) {
                    Node result = AStarFixlayer.aStarFixLayer(i, qubits, locations, dist, layers, pathResult,start);
                    locations = result.getLocations();
                    qubits = result.getQubits();
//                    List<Gate> h_gates = new ArrayList<>();
                    if (i != 0) {
                        for (List<Edge> it : result.getSwaps()) {
                            for (Edge e : it) {
                                Gate cnot = new Gate();
                                cnot.setControl(e.source);
                                cnot.setTarget(e.target);
                                Gate cnot1 = new Gate();
                                cnot1.setControl(e.target);
                                cnot1.setTarget(e.source);
//                            Gate h1 = new Gate();
//                            Gate h2 = new Gate();
//                            if (GraphUtil.contains(graph, e)) {
//                                cnot.setControl(e.source);
//                                cnot.setTarget(e.target);
//                            } else {
//                                cnot.setControl(e.target);
//                                cnot.setTarget(e.source);
//                                int tmp = e.source;
//                                e.source = e.target;
//                                e.target = tmp;
//                                if (!GraphUtil.contains(graph, e)) {
//                                    System.out.println("ERROR: invalid SWAP gate");
//                                    System.exit(-2);
//                                }
//                            }
                                cnot.setType("cx");
                                cnot1.setType("cx");
//                            h1.setType("h");
//                            h2.setType("h");
//                            h1.setControl(-1);
//                            h2.setControl(-1);
//                            h1.setTarget(e.source);
//                            h2.setTarget(e.target);
                                Gate gg = new Gate();
                                gg.setControl(cnot.getControl());
                                gg.setTarget(cnot.getTarget());
                                gg.setType("SWAP");
                                all_gates.add(cnot);
//                            all_gates.add(h1);
//                            all_gates.add(h2);
                                all_gates.add(cnot1);
//                            all_gates.add(h1);
//                            all_gates.add(h2);
                                all_gates.add(cnot);
                                all_gates.add(gg);
                                total_swaps++;
                            }
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
//                            Gate h = new Gate();
//                            h.setControl(-1);
//                            h.setType("h");
//                            h.setTarget(gm.getTarget());
//                            all_gates.add(h);
//                            h_gates.add(h);
//                            h.setTarget(gm.getControl());
//                            all_gates.add(h);
//                            h_gates.add(h);
                                int tmp = gm.getTarget();
                                gm.setTarget(gm.getControl());
                                gm.setControl(tmp);
                            }
                            all_gates.add(gm);
                        }
                    }
//                if (h_gates.size() != 0) {
//                    if (result.getCost_heur() == 0) {
//                        System.out.println("ERROR: invalid heuristic cost!");
//                        System.exit(-2);
//                    }
//
//                    for (Gate it : h_gates) {
//                        all_gates.add(it);
//                    }
//                }
                }
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
                System.out.println();
                of1.append(method + " After mapping (no post mapping optimizations are conducted): " + x);
                of1.append("\n");
                of1.append("  elementary gates: " + (all_gates.size() - total_swaps));
                of1.append("\n");
                of1.append("  depth: " + mapped_circuit.size());
                of1.append("\n");
                System.out.println(method + " After mapping (no post mapping optimizations are conducted): " + x);
                System.out.println("  elementary gates: " + (all_gates.size() - total_swaps));
                System.out.println("  depth: " + mapped_circuit.size());
//        of1.append( ) + "The mapping required " + time + " seconds" );

                of1.append("Initial mapping of the logical qubits (q) to the physical qubits (Q) of the IBM QX3 architecture: ");
                of1.append("\n");

//        cout ) + "The mapping required " + time + " seconds" );

                System.out.println("Initial mapping of the logical qubits (q) to the physical qubits (Q) of the IBM QX3 architecture: ");

                for (int i = 0; i < nqubits; i++) {
                    of1.append("  q" + i + " is initially mapped to Q" + locations.get(i));
                    of1.append("\n");
                    System.out.println("  q" + i + " is initially mapped to Q" + locations.get(i));
                }
                PrintWriter pw=new PrintWriter("../../../src/main/resources/ini_circuits_result/"+argv2+"_FY_"+x);
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

                if (min_gate_nums > (all_gates.size() - total_swaps)) {
                    min_gate_nums = (all_gates.size() - total_swaps);
                    index = x;
                    min_depth = mapped_circuit.size();
                    min_swap_nums = total_swaps;
                }
                printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
            }
        } catch (OutOfMemoryError e){
            printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
            System.out.println(method+" Java heap space。。。。");
            e.printStackTrace();
            return ;
        }catch (OvertimeException e) {
            printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
            e.printStackTrace();
            System.out.println(method + " 超时了");
            return;
        } finally {
            of1.flush();
            of2.flush();
            System.gc();
        }
        return ;

    }


    public static void getMain_GQLResult1(String argv1, String argv2, String iniPath, String method, FileWriter of1, FileWriter of2,
                                          PathResult pathResult, ShortPath[][] dist, FileResult fileResult)  throws IOException {

            Set<Edge> graph = pathResult.getGraph();
            List<List<Gate>> layers = fileResult.getLayers();

            long ngates = fileResult.getNgates();
            int width = 0;
            for (List<Gate> it : layers) {
                if (it.size() > width) {
                    width = it.size();
                }
            }
            of1.append(argv2);
            of1.append("\n");

            of1.append(method+"_main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
            of1.append("\n");
            of1.append("Before mapping: ");
            of1.append("\n");
            of1.append("  elementary gates: " + ngates);
            of1.append("\n");
            of1.append("  depth: " + layers.size());
            of1.append("\n");
            System.out.println(method+" main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
            of1.append("\n");
            System.out.println("Before mapping: ");
            of1.append("\n");
            System.out.println("  elementary gates: " + ngates);
            of1.append("\n");
            System.out.println("  depth: " + layers.size());
            of1.append("\n");
            List<Integer> locations = new ArrayList<>();
            List<Integer> qubits = new ArrayList<>();

            for (int i = 0; i < positions; i++) {
                qubits.add(-1);
                locations.add(-1);
            }

            for (int i = 0; i < nqubits; i++) {
                locations.set(i, i);
                qubits.set(i, i);
            }
            //Initially, no physical qubit is occupied
            for (int i = 0; i < positions; i++) {
                qubits.set(i, -1);
            }
            //Initially, no logical qubit is occupied
            for (int i = 0; i < nqubits; i++) {
                locations.set(i, -1);
            }
            int swap_count = 0;
            Integer min_depth=99999999;
            Integer min_swap_nums=99999999;
            Integer min_gate_nums=99999999;
            Integer index=-1;
            Path mappingPath = Paths.get(iniPath,argv2);
            FileResult ini_mapping = FileUtil.read_ini(mappingPath,dist);
            if (ini_mapping.getLolist() == null || ini_mapping.getLolist().size() <= 0) {
                printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
                return ;
            }

             Boolean flag=false;//标志着是否发生异常 发生异常则为true
            //初始映射
            for (int x = 0; x < ini_mapping.getLolist().size(); x++) {
                for (int z = 0; z < ini_mapping.getLolist().get(x).size(); z++) {
                    locations.set(z, ini_mapping.getLolist().get(x).get(z));
                    qubits.set(z, ini_mapping.getQlist().get(x).get(z));
                }
                try {
                    List<Gate> all_gates = new ArrayList<>();
                    int total_swaps = 0;
                    Date start = new Date(System.currentTimeMillis());
                    for (int i = 0; i < layers.size(); i++) {
                        Node result = AStarFixlayer.aStarFixLayer(i, qubits, locations, dist, layers, pathResult,start);
                        locations = result.getLocations();
                        qubits = result.getQubits();
//                        List<Gate> h_gates = new ArrayList<>();
                        if (i != 0) {
                            for (List<Edge> it : result.getSwaps()) {
                                for (Edge e : it) {
                                    Gate cnot = new Gate();
                                    cnot.setControl(e.source);
                                    cnot.setTarget(e.target);
                                    Gate cnot1 = new Gate();
                                    cnot1.setControl(e.target);
                                    cnot1.setTarget(e.source);
//                            Gate h1 = new Gate();
//                            Gate h2 = new Gate();
//                            if (GraphUtil.contains(graph, e)) {
//                                cnot.setControl(e.source);
//                                cnot.setTarget(e.target);
//                            } else {
//                                cnot.setControl(e.target);
//                                cnot.setTarget(e.source);
//                                int tmp = e.source;
//                                e.source = e.target;
//                                e.target = tmp;
//                                if (!GraphUtil.contains(graph, e)) {
//                                    System.out.println("ERROR: invalid SWAP gate");
//                                    System.exit(-2);
//                                }
//                            }
                                    cnot.setType("cx");
                                    cnot1.setType("cx");
//                            h1.setType("h");
//                            h2.setType("h");
//                            h1.setControl(-1);
//                            h2.setControl(-1);
//                            h1.setTarget(e.source);
//                            h2.setTarget(e.target);
                                    Gate gg = new Gate();
                                    gg.setControl(cnot.getControl());
                                    gg.setTarget(cnot.getTarget());
                                    gg.setType("SWAP");
                                    swap_count += 1;
                                    all_gates.add(cnot);
//                            all_gates.add(h1);
//                            all_gates.add(h2);
                                    all_gates.add(cnot1);
//                            all_gates.add(h1);
//                            all_gates.add(h2);
                                    all_gates.add(cnot);
                                    all_gates.add(gg);
                                    total_swaps++;
                                }
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
//                            Gate h = new Gate();
//                            h.setControl(-1);
//                            h.setType("h");
//                            h.setTarget(gm.getTarget());
//                            all_gates.add(h);
//                            h_gates.add(h);
//                            h.setTarget(gm.getControl());
//                            all_gates.add(h);
//                            h_gates.add(h);
                                    int tmp = gm.getTarget();
                                    gm.setTarget(gm.getControl());
                                    gm.setControl(tmp);
                                }
                                all_gates.add(gm);
                            }
                        }
//                if (h_gates.size() != 0) {
//                    if (result.getCost_heur() == 0) {
//                        System.out.println("ERROR: invalid heuristic cost!");
//                        System.exit(-2);
//                    }
//
//                    for (Gate it : h_gates) {
//                        all_gates.add(it);
//                    }
//                }
                    }
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
                    System.out.println();
                    of1.append(method +" After mapping (no post mapping optimizations are conducted): "+x);
                    of1.append("\n");
                    of1.append("  elementary gates: " + (all_gates.size() - total_swaps));
                    of1.append("\n");
                    of1.append("  depth: " + mapped_circuit.size());
                    of1.append("\n");
                    System.out.println(method +" After mapping (no post mapping optimizations are conducted): "+x);
                    System.out.println("  elementary gates: " + (all_gates.size() - total_swaps));
                    System.out.println("  depth: " + mapped_circuit.size());
//        of1.append( ) + "The mapping required " + time + " seconds" );

                    of1.append("Initial mapping of the logical qubits (q) to the physical qubits (Q) of the IBM QX3 architecture: ");
                    of1.append("\n");

//        cout ) + "The mapping required " + time + " seconds" );

                    System.out.println("Initial mapping of the logical qubits (q) to the physical qubits (Q) of the IBM QX3 architecture: ");

                    for (int i = 0; i < nqubits; i++) {
                        of1.append("  q" + i + " is initially mapped to Q" + locations.get(i));
                        of1.append("\n");
                        System.out.println("  q" + i + " is initially mapped to Q" + locations.get(i));
                    }
                    System.out.println(method + ": " + (all_gates.size() - total_swaps) + " " + mapped_circuit.size() + " " + total_swaps);

                    if (min_gate_nums > (all_gates.size() - total_swaps)) {
                        min_gate_nums = (all_gates.size() - total_swaps);
                        index = x;
                        min_depth = mapped_circuit.size();
                        min_swap_nums = total_swaps;

                    }
                    PrintWriter pw=new PrintWriter("../../../src/main/resources/ini_circuits_result/"+argv2+"_GQL_"+x);
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

                    if (min_swap_nums==0){
                        break;
                    }
                    System.gc();
                }catch (OutOfMemoryError e){
                    if(x==ini_mapping.getLolist().size()-1){
                        flag=true;
                        printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
                    }
                    System.out.println(method+" Java heap space。。。。");
                    e.printStackTrace();
                    continue;
                }catch (OvertimeException e){
                    if(x==ini_mapping.getLolist().size()-1){
                        flag=true;
                        printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
                    }
                    e.printStackTrace();
                    System.out.println(method+" 超时了。。。。");
                    continue;
                }catch (Exception e){
                    if(x==ini_mapping.getLolist().size()-1){
                        flag=true;
                        printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
                    }
                    e.printStackTrace();
                    System.out.println(method+" 出現了其他錯誤。。。。");
                    continue;
                }finally {
                    of1.flush();
                    of2.flush();
                    System.gc();
                }
            }
            if (!flag){
                printInfo(of1,of2,method,min_gate_nums,min_depth,min_swap_nums);
                of1.flush();
                of2.flush();
            }

        }


        public static void printInfo( FileWriter of1, FileWriter of2,String method,Integer min_gate_nums,
                                      Integer min_depth,Integer min_swap_nums) throws IOException {
            of2.append(method+": " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
            of2.append("\n");
            of1.append(method+": " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
            of1.append("\n");
            System.out.println(method+": " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
        }
}
