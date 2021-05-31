package cn.ecnu.tabusearch.run;

import cn.ecnu.tabusearch.Edge;
import cn.ecnu.tabusearch.ShortPath;
import cn.ecnu.tabusearch.exception.OvertimeException;
import cn.ecnu.tabusearch.swaps.AStarFixlayer;
import cn.ecnu.tabusearch.swaps.Gate;
import cn.ecnu.tabusearch.swaps.Node;
import cn.ecnu.tabusearch.utils.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class main_Zulehner {
    private static Integer nqubits = 20;
    private static Integer positions = 20;
//    public static void printList(List<Integer> list){
//        for(Integer i: list){
//            System.out.print(i+" ");
//        }
//        System.out.println();
//    }
    public static void main(String[] args) throws IOException, OvertimeException {
        String filePath="E:\\github\\QubitMappingInTabu\\examples_result\\";
        String gql_out1="E:\\github\\quantum_compiler_optim\\results\\total1";
        String gql_out2="E:\\github\\quantum_compiler_optim\\results\\total";
        String Zulehner_out1="E:\\github\\quantum_compiler_optim\\results\\total1";
        String Zulehner_out2="E:\\github\\quantum_compiler_optim\\results\\total";
        String FY_out1="E:\\github\\quantum_compiler_optim\\results\\total1";
        String FY_out2="E:\\github\\quantum_compiler_optim\\results\\total";
        String GQL_iniPath="E:\\github\\quantum_mapping_a_-\\ini_mapping_q20\\";
        String grapPath="E:\\github\\quantum_mapping_a_-\\pre_result\\";
        String Zulehner_iniPath="";
        String FY_iniPath="E:\\github\\quantum_mapping_a_-\\testIni\\";
//        getMainZulehnerResult(filePath,Zulehner_iniPath,"main_Zulehner",Zulehner_out1,Zulehner_out2);
//        getMain_GQLResult(filePath,GQL_iniPath,"main_GQL",gql_out1,gql_out2);
        getMain_FYResult(filePath,FY_iniPath,"main_FY_Zulehner",FY_out1,FY_out2);

    }
    public static void getMainZulehnerResult(String filePath,String iniPath,String method,String out1,String out2) throws IOException, OvertimeException {
        //"E:\\github\\quantum_mapping_a_-\\examples_result\\"
        List<String> files = FileUtil.getFiles(filePath);
        FileWriter of1 = new FileWriter(out1, true);
        FileWriter of2 = new FileWriter(out2, true);
        for (int k = 0; k < files.size(); k++) {

            StringBuilder str = new StringBuilder(files.get(k));
            str.delete(0, str.lastIndexOf("/") + 1);
            String ss = str.substring(0, str.lastIndexOf("."));
            System.out.println("处理文件： " + ss);

            String argv1 = files.get(k);
            String argv2 = ss;
            PathResult pathResult = PathUtil.build_graph_QX20();
            Set<Edge> graph = pathResult.getGraph();
            ShortPath[][] dist = PathUtil.build_dist_table(graph);
            Path qasm = Paths.get(argv1);

            FileResult fileResult = FileUtil.read_qasm(qasm, argv2);
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
            of1.append("main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
            of1.append("\n");
            of1.append("Before mapping: ");
            of1.append("\n");
            of1.append("  elementary gates: " + ngates);
            of1.append("\n");
            of1.append("  depth: " + layers.size());
            of1.append("\n");
            System.out.println("main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
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
            if (iniPath.equals("")){

            }
            Path mappingPath = Paths.get(iniPath,ss);
            FileResult ini_mapping = FileUtil.read_ini(mappingPath,dist);
            if (ini_mapping.getLolist() == null || ini_mapping.getLolist().size() <= 0) {
                continue;
            }
            //初始映射
            for (int x = 0; x < ini_mapping.getLolist().size(); x++) {
                if (!method.equals("main_Zulehner")){
                    for (int z = 0; z < ini_mapping.getLolist().get(x+1).size(); z++) {
                        locations.set(z, ini_mapping.getLolist().get(x+1).get(z));
                        qubits.set(z, ini_mapping.getQlist().get(x+1).get(z));
                    }
                }


                Date start = new Date(System.currentTimeMillis());
                for (int i = 0; i < layers.size(); i++) {
                    Node result = AStarFixlayer.aStarFixLayer(i, qubits, locations, dist, layers, pathResult,start);
                    locations = result.getLocations();
                    qubits = result.getQubits();
                    List<Gate> h_gates = new ArrayList<>();
                    if (i != 0) {
                        for (List<Edge> it : result.getSwaps()) {
                            for (Edge e : it) {
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
                                swap_count += 1;
                                all_gates.add(cnot);
                                all_gates.add(h1);
                                all_gates.add(h2);
                                all_gates.add(cnot);
                                all_gates.add(h1);
                                all_gates.add(h2);
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
                        if (result.getCost_heur() == 0) {
                            System.out.println("ERROR: invalid heuristic cost!");
                            System.exit(-2);
                        }

                        for (Gate it : h_gates) {
                            all_gates.add(it);
                        }
                    }
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
                of2.append(method+" " + (all_gates.size() - total_swaps) + " " + mapped_circuit.size() + " " + total_swaps);
                of2.append("\n");
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
                of1.flush();
                of2.flush();
                System.gc();
            }
        }
        of1.close();
        of2.close();
    }

    public static void getMain_GQLResult(String filePath,String iniPath,String method,String out1,String out2) throws IOException {
        //"E:\\github\\quantum_mapping_a_-\\examples_result\\"
        List<String> files = FileUtil.getFiles(filePath);
        FileWriter of1 = new FileWriter(out1, true);
        FileWriter of2 = new FileWriter(out2, true);
        for (int k = 0; k < files.size(); k++) {

            StringBuilder str = new StringBuilder(files.get(k));
            str.delete(0, str.lastIndexOf("/") + 1);
            String ss = str.substring(0, str.lastIndexOf("."));
            System.out.println("处理文件： " + ss);

            String argv1 = files.get(k);
            String argv2 = ss;
            PathResult pathResult = PathUtil.build_graph_QX20();
            Set<Edge> graph = pathResult.getGraph();
            ShortPath[][] dist = PathUtil.build_dist_table(graph);
            Path qasm = Paths.get(argv1);

            FileResult fileResult = FileUtil.read_qasm(qasm, argv2);
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
            of1.append("main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
            of1.append("\n");
            of1.append("Before mapping: ");
            of1.append("\n");
            of1.append("  elementary gates: " + ngates);
            of1.append("\n");
            of1.append("  depth: " + layers.size());
            of1.append("\n");
            System.out.println("main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
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
            Integer min_depth=99999999;
            Integer min_swap_nums=99999999;
            Integer min_gate_nums=99999999;
            Integer index=-1;
            Path mappingPath = Paths.get(iniPath,ss);
            FileResult ini_mapping = FileUtil.read_ini(mappingPath,dist);
            if (ini_mapping.getLolist() == null || ini_mapping.getLolist().size() <= 0) {
                continue;
            }

            //初始映射
            for (int x = 1; x < ini_mapping.getLolist().size(); x++) {
                    for (int z = 0; z < ini_mapping.getLolist().get(x).size(); z++) {
                        locations.set(z, ini_mapping.getLolist().get(x).get(z));
                        qubits.set(z, ini_mapping.getQlist().get(x).get(z));
                    }
                    try {
                        Date start = new Date(System.currentTimeMillis());
                        for (int i = 0; i < layers.size(); i++) {
                            Node result = AStarFixlayer.aStarFixLayer(i, qubits, locations, dist, layers, pathResult,start);
                            locations = result.getLocations();
                            qubits = result.getQubits();
                            List<Gate> h_gates = new ArrayList<>();
                            if (i != 0) {
                                for (List<Edge> it : result.getSwaps()) {
                                    for (Edge e : it) {
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
                                        swap_count += 1;
                                        all_gates.add(cnot);
                                        all_gates.add(h1);
                                        all_gates.add(h2);
                                        all_gates.add(cnot);
                                        all_gates.add(h1);
                                        all_gates.add(h2);
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
                                if (result.getCost_heur() == 0) {
                                    System.out.println("ERROR: invalid heuristic cost!");
                                    System.exit(-2);
                                }

                                for (Gate it : h_gates) {
                                    all_gates.add(it);
                                }
                            }
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
                        if (min_swap_nums > (all_gates.size() - total_swaps)) {
                            min_gate_nums = (all_gates.size() - total_swaps);
                            index = x;
                            min_depth = mapped_circuit.size();
                            min_swap_nums = total_swaps;

                        }
                        System.gc();
                    }catch (OutOfMemoryError e){
                            of2.append(method+" :" + min_gate_nums + " " + min_depth + " " + min_swap_nums);
                            of2.append("\n");
                            of1.append(method+" :" + min_gate_nums + " " + min_depth + " " + min_swap_nums);
                            of1.append("\n");
                            System.out.println(method+" :" + min_gate_nums + " " + min_depth + " " + min_swap_nums);
                            of1.flush();
                            of2.flush();
                        System.out.println(method+" Java heap space。。。。");
                        e.printStackTrace();
                        continue;
                    }catch (OvertimeException e){
                        e.printStackTrace();
                        System.out.println(method+" 超时了。。。。");
                        continue;
                    }
            }
            of2.append(method+" " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
            of2.append("\n");
            of1.append(method+" " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
            of1.append("\n");
            System.out.println(method+" " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
            of1.flush();
            of2.flush();
        }
        of1.close();
        of2.close();
    }

    public static void getMain_FYResult(String filePath,String iniPath,String method,String out1,String out2) throws IOException, OvertimeException {
        //"E:\\github\\quantum_mapping_a_-\\examples_result\\"
        List<String> files = FileUtil.getFiles(filePath);
        FileWriter of1 = new FileWriter(out1, true);
        FileWriter of2 = new FileWriter(out2, true);
        for (int k = 0; k < files.size(); k++) {

            StringBuilder str = new StringBuilder(files.get(k));
            str.delete(0, str.lastIndexOf("/") + 1);
            String ss = str.substring(0, str.lastIndexOf("."));
            System.out.println("处理文件： " + ss);

            String argv1 = files.get(k);
            String argv2 = ss;
            PathResult pathResult = PathUtil.build_graph_QX20();
            Set<Edge> graph = pathResult.getGraph();
            ShortPath[][] dist = PathUtil.build_dist_table(graph);
            Path qasm = Paths.get(argv1);

            FileResult fileResult = FileUtil.read_qasm(qasm, argv2);
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
            of1.append("main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
            of1.append("\n");
            of1.append("Before mapping: ");
            of1.append("\n");
            of1.append("  elementary gates: " + ngates);
            of1.append("\n");
            of1.append("  depth: " + layers.size());
            of1.append("\n");
            System.out.println("main_Circuit name: " + argv1 + " (requires " + nqubits + " qubits)");
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
            Integer min_depth=9999999;
            Integer min_swap_nums=9999999;
            Integer min_gate_nums=9999999;
            Integer index=-1;
            Path mappingPath = Paths.get(iniPath,ss+".qasm.txt");
            FileResult ini_mapping = FileUtil.readFYIni(mappingPath);
            if (ini_mapping.getLolist() == null || ini_mapping.getLolist().size() <= 0) {
                continue;
            }
            //初始映射
            for (int x = 0; x < ini_mapping.getLolist().size(); x++) {
                for (int z = 0; z < ini_mapping.getLolist().get(x).size(); z++) {
                    locations.set(z, ini_mapping.getLolist().get(x).get(z));
                    qubits.set(z, ini_mapping.getQlist().get(x).get(z));
                }
                for (int i = 0; i < layers.size(); i++) {
                    Date start = new Date(System.currentTimeMillis());
                    Node result = AStarFixlayer.aStarFixLayer(i, qubits, locations, dist, layers, pathResult,start);
                    locations = result.getLocations();
                    qubits = result.getQubits();
                    List<Gate> h_gates = new ArrayList<>();
                    if (i != 0) {
                        for (List<Edge> it : result.getSwaps()) {
                            for (Edge e : it) {
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
                                swap_count += 1;
                                all_gates.add(cnot);
                                all_gates.add(h1);
                                all_gates.add(h2);
                                all_gates.add(cnot);
                                all_gates.add(h1);
                                all_gates.add(h2);
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
                        if (result.getCost_heur() == 0) {
                            System.out.println("ERROR: invalid heuristic cost!");
                            System.exit(-2);
                        }

                        for (Gate it : h_gates) {
                            all_gates.add(it);
                        }
                    }
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
                if (min_swap_nums>(all_gates.size() - total_swaps)){
                    min_gate_nums= (all_gates.size() - total_swaps);
                    index=x;
                    min_depth=mapped_circuit.size();
                    min_swap_nums=total_swaps;
                }
                System.gc();
            }
            of2.append(method+" " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
            of2.append("\n");
            of1.append(method+" " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
            of1.append("\n");
            System.out.println(method+" " + min_gate_nums + " " + min_depth + " " + min_swap_nums);
            of1.flush();
            of2.flush();
        }
        of1.close();
        of2.close();
    }

}
