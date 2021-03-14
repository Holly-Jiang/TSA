package cn.ecnu.tabusearch.run;

import cn.ecnu.tabusearch.*;
import cn.ecnu.tabusearch.exception.OvertimeException;
import cn.ecnu.tabusearch.swaps.Gate;
import cn.ecnu.tabusearch.translate.Instruction;
import cn.ecnu.tabusearch.translate.Level;
import cn.ecnu.tabusearch.translate.Translate;
import cn.ecnu.tabusearch.utils.DateUtil;
import cn.ecnu.tabusearch.utils.FileResult;
import cn.ecnu.tabusearch.utils.FileUtil;
import cn.ecnu.tabusearch.utils.PathUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MyTabuSearch {
    //当前的所有SWAP方案
    private static Integer positions = -1;
    private static Set<Edge> graph = new HashSet<>();
    private static ShortPath dist[][];
    private static Integer tabuListSize = 5;
    private static Integer maxIterations = 100;
    public void printList(List<Integer> list) {
        for (Integer i : list) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
    public void printCurcuits(List<Gate> list) {
        for (Gate i : list) {
            System.out.println(i.getType() + " "+i.getControl()+" "+i.getTarget()+";");
        }
    }
    public static void writeCurcuits(List<Gate> list, FileWriter fw) throws IOException {
        for (Gate i : list) {
            if (i.getControl()+1==0){
                if (i.getType().equals("rz")){
                    fw.write(i.getType()+"("+i.getAngle()+")" +" q["+i.getTarget()+"];\n");
                    continue;
                }
                fw.write(i.getType() +" q["+i.getTarget()+"];\n");
                continue;

            }
            fw.write(i.getType() + " q["+i.getControl()+"],q["+i.getTarget()+"];\n");
        }
        fw.flush();
    }

    public static void main(String[] args)  throws IOException {
        if (args.length<3){
            System.out.println("usage: java -jar tabusearch.jar [connect/degree] [small/medium/large/all] [num/depth]");
            return ;
        }
        int type=0;
        if (args[2].equals("depth")){
            type=1;
        }
        Path outPath = Paths.get("../../../src/main/resources/compare", "total");
        FileWriter iniWriter = new FileWriter(outPath.toFile(), true);
        List<String> files = FileUtil.getFiles("../../../src/main/resources/examples_result/");
        graph = PathUtil.build_graph_QX20().getGraph();
        dist = PathUtil.build_dist_table_tabu(graph);
        Date start = new Date(System.currentTimeMillis());
        System.out.println("--------------------------------");
        System.out.println("starttime: "+start);
        for (int k = 0; k < files.size(); k++) {
            List<Solution> solutions;
            Integer tabuListSize = 10;
            Path read_Path = Paths.get(files.get(k));
            StringBuilder str = new StringBuilder(files.get(k));

//             read_Path = Paths.get("../../../src/main/resources/examples_result/0example.qasm");
//             str = new StringBuilder("../../../src/main/resources/examples_result/0example.qasm");
            str.delete(0, str.lastIndexOf("/") + 1);
            String ss = str.substring(0, str.lastIndexOf("."));
            System.out.println("处理第 "+k+" 个文件： " + str.toString());
            //随即得到一个初始解
            Integer min_swaps = 999999999;
            Integer min_index=-1;

            Path mappingPath = Paths.get("../../../src/main/resources/ini_mapping_q20/", ss);

            //Initial Mapping
            FileResult ini_mapping = FileUtil.read_ini(mappingPath, dist);
            if (ini_mapping.getLolist() == null || ini_mapping.getLolist().size() <= 0) {
                continue;
            }
            FileResult fileResult = FileUtil.read_qasm(read_Path, ss);

            if (args[1].equals("small")){
                if (fileResult.getN2gates()>=100){
                    continue;
                }
            }else  if (args[1].equals("medium")){
                if (fileResult.getN2gates()<100||fileResult.getN2gates()>1000){
                    continue;
                }
            }else if (args[1].equals("large")){
                if (fileResult.getN2gates()<=1000){
                    continue;
                }
            }


            List<List<Gate>> layers = fileResult.getLayers();
            //初始映射
            for (int x = 0; x < ini_mapping.getLolist().size(); x++) {
                try {
                    List<Edge> swaps = new ArrayList<>();
                    //当前的映射关系
                    //index表示逻辑qubit locations[index]表示逻辑qubit映射的物理qubit位置,-1表示没有进行映射
                    List<Integer> locations = new ArrayList<>();
                    //index表示物理qubit，qubits[index]表示物理qubit映射的哪个逻辑qubit，-1 表示没有进行映射
                    List<Integer> qubits = new ArrayList<>();

                    //应该初始化为-1
                    for (int i = 0; i < 20; i++) {
                        locations.add(i, -1);
                        qubits.add(i, -1);
                    }
                    //最终电路输出到文件
                    Path tabuResultPath = Paths.get("../../../src/main/resources/total_tabu_lookahead2",
                            ss + "_" + x + ".qasm");
                    FileWriter resultWriter = new FileWriter(tabuResultPath.toFile(), false);
                    resultWriter.write("OPENQASM 2.0;\n" +
                            "include \"qelib1.inc\";\n" +
                            "qreg q[16];\n" +
                            "creg c[16];\n");
                    for (int z = 0; z < ini_mapping.getLolist().get(x).size(); z++) {
                        locations.set(z, ini_mapping.getLolist().get(x).get(z));
                        qubits.set(z, ini_mapping.getQlist().get(x).get(z));
                    }
                    TabuSearch ts = setupTS(tabuListSize, maxIterations);
                    for (int d = 0; d < layers.size(); d++) {

                        List<Gate> all_gates = new ArrayList<>();
                        List<Gate> currentLayers = new ArrayList<>();
                        for (int s = 0; s < layers.get(d).size(); s++) {
                            if (layers.get(d).get(s).getControl() != -1) {
                                currentLayers.add(layers.get(d).get(s));
                            } else {
                                all_gates.add(layers.get(d).get(s));
                            }
                        }
                        //前瞻层的2-qubits门
                        List<Gate> nextLayers_1 = new ArrayList<>();
                        int lookNum=1;
                        while(lookNum<3){
                            for (int s = 0; d+lookNum < layers.size() - 1 && s < layers.get(d + lookNum).size(); s++) {
                                if (layers.get(d + lookNum).get(s).getControl() != -1) {
                                    nextLayers_1.add(layers.get(d + lookNum).get(s));
                                }
                            }
                            lookNum++;
                        }

                        if (currentLayers.size() <= 0) {
                            writeCurcuits(all_gates, resultWriter);
                            continue;
                        }
                        MySolution initialSolution = new MySolution(graph, dist, new ArrayList<>(locations), new ArrayList<>(qubits), currentLayers, nextLayers_1);
                        initialSolution.getCircuits().addAll(all_gates);
                        NeighborResult neighborResult = buildInstance(currentLayers, dist, qubits, locations, nextLayers_1, initialSolution,type);
                        solutions=neighborResult.getSolutions();
                        if (solutions.size() > 0) {
                            initialSolution = (MySolution) solutions.get(0);
                        } else {
                            writeCurcuits(all_gates, resultWriter);
                            writeCurcuits(neighborResult.getCurr_solved_gates(), resultWriter);
                            continue;
                        }
                        Integer maxIterations = new Double(solutions.size() * 0.5).intValue() + 1;

                        //Tabu search
                        MySolution returnValue = (MySolution) ts.run(initialSolution,type);
                        if (returnValue == null) {
                            writeCurcuits(initialSolution.getCircuits(), resultWriter);
                            throw new OvertimeException("沒有候选集");
                        }
                        locations = returnValue.getLocations();
                        qubits = returnValue.getQubits();
                        swaps.addAll(returnValue.getSwaps());
                        writeCurcuits(returnValue.getCircuits(), resultWriter);
                    }
                    if (min_swaps > swaps.size()) {
                        min_swaps = swaps.size();
                        min_index = x;
                    }
                    resultWriter.close();
                    //交换次数等于0 就可以不用再找更小花费的交换了
                    if (min_swaps == 0) {
                        break;
                    }
                }catch (OvertimeException e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    continue;
                }
            }

            iniWriter.append(ss);
            iniWriter.append("\n");
            //最小的GQL文件index 初始2-qubits门数量 生成电路的2-qubit门数量  生成电路层数  最小交换次数  +fileResult.getN2gates()+" "
            FileResult min_files = FileUtil.read_qasm_to_compute_depth(
                    Paths.get("../../../src/main/resources/total_tabu_lookahead2/"+ss+"_"+min_index+".qasm"));
            iniWriter.append(min_index+" "+fileResult.getN2gates()+" "+min_files.getN2gates()+" "+min_files.getLayers().size() +" " + min_swaps);
            iniWriter.append("\n");
            iniWriter.flush();
//            System.out.println("最小交换："+min_index+" " + min_swaps);
        }

        Date end = new Date(System.currentTimeMillis());
        iniWriter.append("time： "+DateUtil.TimeDifference(start, end)+" seconds");
        System.out.println("time： "+DateUtil.TimeDifference(start, end)+" seconds");
        System.out.println("endtime: "+end);
        System.out.println("--------------------------------");

        iniWriter.close();
    }
//    Integer computeSolutionValue( Set<Edge> graph,)

    //Breadth first search algorithm to determine the shortest paths between two physical qubits
    //初始交换方案
    public static NeighborResult  buildInstance(List<Gate> currentLayers, ShortPath dist[][], List<Integer> qubits,
                                        List<Integer> locations, List<Gate> nextLayers_1, MySolution parent,int type) {
        List<Solution> solutions = new ArrayList<>();
        //当前利用的节点
        //选取和当前使用节点交集最多的路径作为最短路径
        /**
         * 计算邻域
         * 遍历初始给定的门关系
         * 然后计算几对门的最短路径，每条路径中的每一步都是一次候选交换
         * 顺便计算每条边的权重
         */

        NeighborResult neighborResult = MySolution.computeNeighbor(graph,parent, dist, qubits, locations, currentLayers, nextLayers_1,type);
        solutions = neighborResult.getSolutions();

        /**
         * 邻域是指当前这个状态可以进行的所有与这些节点
         * 至少有一个点连接的边的SWAP操作
         *
         * 领域动作采用Floyd算法计算图上的所有节点的距离，
         * 然后对图上的每条边进行权重计算
         * 当前状态的目标节点之间经过的路径权重加1
         * 然后根据权重对边进行排序
         *
         */
        //计算
//        MySolution.computeWeight(neighborResult.getChoose_path(),graph);
        //region计算邻域
//        for (int i = 0; i < solutions.size(); i++) {
//            MySolution solution = (MySolution) solutions.get(i);
//            solution.getNeighbors();
//        }
        //endregion
        //邻域动作
        /**
         * 根据边的权重进行排序
         */
        Collections.sort(solutions, new Comparator<Solution>() {
            @Override
            public int compare(Solution a, Solution b) {
                return a.getValue().compareTo(b.getValue());
            }
        });
        return neighborResult;
    }

    public static TabuSearch setupTS(Integer tabuListSize, Integer iterations) {
        return new TabuSearch(new MyTabuList(tabuListSize), new MyStopCondition(iterations), new MyNeighborSolutionLocator());
    }
}
