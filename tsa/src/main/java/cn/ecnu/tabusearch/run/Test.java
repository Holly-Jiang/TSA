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
import cn.ecnu.vf2.core.MyVF2;
import cn.ecnu.vf2.graph.IniGraph;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Test {
    private static Integer positions = -1;
    private static Set<Edge> graph = new HashSet<>();
    private static ShortPath dist[][];
    private static Integer tabuListSize = 5;
    private static Integer maxIterations = 100;
    public static final Integer N=999999;
    public static void translate(String inPath){
        /**
         * /home/test/qubitmapping/
         * E:\github\Tabu_win\tabu\src\main\resources\example\
         */
        //String inPath = "../../../src/main/resources/example/";//遍历文件夹下的所有.jpg文件
//            File file = new File("E:\\github\\Tabu_win\\tabu\\src\\main\\resources\\example\\0example.qasm");
            File file = new File(inPath);
            BufferedReader reader = null;
            Level[] tower=new Level[N];
            for (int k=0;k<N;k++){
                tower[k]=new Level(k);
            }
            try {
                reader = new BufferedReader(new FileReader(file));
                Translate trans=new Translate();
                System.out.println("deal file: "+inPath);
                int total_level = trans.translate(tower,reader);
                StringBuffer sb=new StringBuffer();
                sb.append("../../../src/main/resources/examples_result/");
                String []splits=inPath.split("/");
                sb.append(splits[splits.length-1]);

                FileWriter fw = new FileWriter(sb.toString(), false);
                PrintWriter pw = new PrintWriter(fw);
                for (int j = 1; j <= total_level; j++) {
                    Instruction p = tower[j].head.getNext();
                    while (p!=null) {
                        p.print(pw);
                        p = p.getNext();
                    }
                }
                pw.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        return ;
    }
    public static void iniComplete(String filepath,int type) throws FileNotFoundException {
        Path graphPath = Paths.get("../../../src/main/resources/graphDB", "mygraphdb3.data");
        Path queryPath = Paths.get("E:/github/VF2/data/graphDB", "Ex2.my");
        Path outPath = Paths.get("../../../src/main/resources/graphDB", "res_Ex2.my");
        List<String> files=FileUtil.getFiles("../../../src/main/resources/pre_ini_qx20");
        Path outIniPath = Paths.get("E:/github/VF2/data/graphDB", "ini_map_GQL.my");
        Path mappingPath = Paths.get("E:/github/SubgraphComparing/build/matching", "res.dat");
        System.out.println(filepath+".qasm--------------------------");
        StringBuilder str = new StringBuilder(filepath);
        String ss = str.delete(0, str.lastIndexOf("/") + 1).toString();
            mappingPath = Paths.get("../../../src/main/resources/pre_ini_qx20/"+ss);
            queryPath = Paths.get("../../../src/main/resources/pre_result/"+ss);
            outIniPath=Paths.get("../../../src/main/resources/ini_mapping_q20/"+ss);
            System.out.println("Target Graph Path: " + graphPath.toString());
            System.out.println("Query Graph Path: " + queryPath.toString());
            System.out.println("Output Path: " + outPath.toString());
            System.out.println("OutIniput Path: " + outIniPath.toString());
            System.out.println();
            PrintWriter iniWriter = new PrintWriter(outIniPath.toFile());

            ArrayList<IniGraph> graphSet = FileUtil.loadGraphSetFromFile(graphPath, "Graph ");
            ArrayList<IniGraph> querySet =FileUtil.loadGraphSetFromFile(queryPath, "Query ");
            List<List<Integer>> mapping=FileUtil.loadDataSetFromFile(mappingPath,"mapping");

            int maxMappingCount=0;
            Map<List<Integer>,Integer> mappingResult=new HashMap<>();
            for (int i=0;i<mapping.size();i++){
                int m=0;
                for (int j=0;j<mapping.get(i).size();j++){
                    if (mapping.get(i).get(j)-99999!=0){
                        m++;
                    }
                }
                mappingResult.put(mapping.get(i),m);
                if (m-maxMappingCount>0){
                    maxMappingCount=m;
                }
            }
            MyVF2 vf2= new MyVF2();

            System.out.println("Loading Done!");
            System.out.println();
            for (IniGraph queryGraph : querySet){
                List<List<Integer>> stateSet = vf2.delData(graphSet, queryGraph,mappingResult,type);

                for (int i=0;i<stateSet.size();i++){
                    iniWriter.write("t "+i);
                    iniWriter.write("\n");
                    for (int j=0;j<stateSet.get(i).size();j++){
                        iniWriter.write(stateSet.get(i).get(j)+" "+j);
                        iniWriter.write("\n");
                    }
                }
            }

            iniWriter.write("t # -1");
            iniWriter.flush();
            iniWriter.close();
    }
    public static void search(String filepath,int type) throws IOException {
        Path outPath = Paths.get("../../../src/main/resources/compare", "total");
        FileWriter iniWriter = new FileWriter(outPath.toFile(), true);
        graph = PathUtil.build_graph_QX20().getGraph();
        dist = PathUtil.build_dist_table_tabu(graph);
        Date start = new Date(System.currentTimeMillis());
        System.out.println("starttime: "+start);
            List<Solution> solutions;
            Integer tabuListSize = 10;
            Path read_Path = Paths.get(filepath+".qasm");
            StringBuilder str = new StringBuilder(filepath);
            String ss = str.delete(0, str.lastIndexOf("/") + 1).toString();
            System.out.println("处理文件： " + ss);
            //随即得到一个初始解
            Integer min_swaps = 999999999;
            Integer min_index=-1;
            Path mappingPath = Paths.get("../../../src/main/resources/ini_mapping_q20/", ss);
            //Initial Mapping
            FileResult ini_mapping = FileUtil.read_ini(mappingPath, dist);
            if (ini_mapping.getLolist() == null || ini_mapping.getLolist().size() <= 0) {
              return;
            }
            FileResult fileResult = FileUtil.read_qasm(read_Path, ss);
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
                    TabuSearch ts = MyTabuSearch.setupTS(tabuListSize, maxIterations);
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
                            MyTabuSearch.writeCurcuits(all_gates, resultWriter);
                            continue;
                        }
                        MySolution initialSolution = new MySolution(graph, dist, new ArrayList<>(locations), new ArrayList<>(qubits), currentLayers, nextLayers_1);
                        initialSolution.getCircuits().addAll(all_gates);
                        NeighborResult neighborResult = MyTabuSearch.buildInstance(currentLayers, dist, qubits, locations, nextLayers_1, initialSolution,type);
                        solutions=neighborResult.getSolutions();
                        if (solutions.size() > 0) {
                            initialSolution = (MySolution) solutions.get(0);
                        } else {
                            MyTabuSearch.writeCurcuits(all_gates, resultWriter);
                            MyTabuSearch.writeCurcuits(neighborResult.getCurr_solved_gates(), resultWriter);
                            //说明当前的映射已满足currentLayers的2-qubits门
                            continue;
                        }
                        Integer maxIterations = new Double(solutions.size() * 0.5).intValue() + 1;

                        //Tabu search
                        MySolution returnValue = (MySolution) ts.run(initialSolution, type);
                        if (returnValue == null) {
                            MyTabuSearch.writeCurcuits(initialSolution.getCircuits(), resultWriter);
                            throw new OvertimeException("沒有候选集");
                        }
                        locations = returnValue.getLocations();
                        qubits = returnValue.getQubits();
                        swaps.addAll(returnValue.getSwaps());
                        MyTabuSearch.writeCurcuits(returnValue.getCircuits(), resultWriter);
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
            Date end = new Date(System.currentTimeMillis());
            //最小的GQL文件index 初始2-qubits门数量 生成电路的2-qubit门数量  生成电路层数  最小交换次数  +fileResult.getN2gates()+" "
            FileResult min_files = FileUtil.read_qasm_to_compute_depth(
                    Paths.get("../../../src/main/resources/total_tabu_lookahead2/"+ss+"_"+min_index+".qasm"));
            //
            iniWriter.append(min_index+" "+fileResult.getN2gates()+" "+min_files.getN2gates()+" "+min_files.getLayers().size()
                    +" " + min_swaps+" "+DateUtil.TimeDifference(start, end));
            System.out.println("mapping index: "+min_index+" the  number of initial 2-qubit gates: "+fileResult.getN2gates()
                    +" the  number of result 2-qubit gates: "+min_files.getN2gates()
                    +" result depth: "+min_files.getLayers().size()
                +" minimum number of swaps: " + min_swaps+" time spent: "+DateUtil.TimeDifference(start, end));
            iniWriter.append("\n");
            iniWriter.flush();

        System.out.println("time： "+DateUtil.TimeDifference(start, end)+" seconds");
        System.out.println("endtime: "+end);
        iniWriter.close();
    }
    public static String executeLinuxCmd(String cmd) {
        System.out.println("执行命令[ " + cmd + "]");
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            String line;
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer out = new StringBuffer();
            while ((line = stdoutReader.readLine()) != null ) {
                out.append(line+"\n");
            }
            System.out.println("輸出："+out.toString());
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
}
    public static void main(String[] args) {
        if (args.length<2){
            System.out.println("usage: java -jar tabusearch.jar [input file path] [connect/degree] [num/depth]");
            return ;
        }
        System.out.println(args.length);
        for (int i=0;i<args.length;i++){
            System.out.println(args[i]);
        }
        StringBuilder str = new StringBuilder(args[0]);
        str.delete(0, str.lastIndexOf("/") + 1);
        String ss = str.substring(0, str.lastIndexOf("."));
        System.out.println("handle the file： " + ss);
        String argv1 =args[0];
        String argv2 = ss;
        String filepath=argv1.substring(0, argv1.lastIndexOf("."));
        //region adjust life time
        Path qasm = Paths.get(argv1);
        //Adjust the life cycle of qubits
        System.out.println("--------------adjust the life cycle of qubits start--------------");
        translate(argv1);
        try {
            //generate the DAG
            FileUtil.precessReadQasm(qasm, argv2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("--------------adjust the life cycle of qubits end--------------");
        //endregion
        //region initial mapping
        System.out.println("--------------cmd start--------------");
        executeLinuxCmd("../../../../CISC/SubgraphComparing/build/matching/SubgraphMatching.out -d ../../../../CISC/SubgraphComparing/test/sample_dataset/test_case_1.graph -q ../../../src/main/resources/pre_result/"+ss+" -filter GQL -order GQL -engine LFTJ -num 100");
        System.out.println("--------------cmd end--------------");
        System.out.println("--------------complete start--------------");
        //complete
         try {
             int type=0;
             if (args[1].equals("degree")){
                 type=1;
             }
                     iniComplete("../../../src/main/resources/pre_ini_qx20/"+ss,type);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        System.out.println("--------------complete end--------------");
        //endregion

        //region search

        System.out.println("--------------search start--------------");
        try {
            int type1=0;
            if (args[2].equals("depth")){
                type1=1;
            }
            search(filepath,type1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("--------------search end--------------");
        //endregion
    }
}
