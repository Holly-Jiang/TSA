package cn.ecnu.tabusearch.run;

import cn.ecnu.sc.core.MyVF2;
import cn.ecnu.sc.graph.IniGraph;
import cn.ecnu.tabusearch.utils.FileUtil;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static cn.ecnu.tabusearch.run.Test.executeLinuxCmd;

public class IniMappingComplete {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("-----------------------");
        if (args.length == 0) {
            printUsage();
            System.out.println();
            System.out.println("Warning: no arguments given, using default arguments");
            System.out.println();
            return;
        }
        int type=0;
        if (args[0].equals("connect")){
            type=0;
        }else if (args[0].equals("connect")){
            type=1;
        }else{
            printUsage();
        }
//            dealCMD();
        Path graphPath = Paths.get("../../../src/main/resources/graphDB", "mygraphdb3.data");
        Path queryPath = Paths.get("E:/github/VF2/data/graphDB", "Ex2.my");
        Path outPath = Paths.get("../../../src/main/resources/graphDB", "res_Ex2.my");
        List<String> files= FileUtil.getFiles("../../../src/main/resources/pre_ini_qx20");
        Path outIniPath = Paths.get("E:/github/VF2/data/graphDB", "ini_map_GQL.my");
        Path mappingPath = Paths.get("E:/github/SubgraphComparing/build/matching", "res.dat");
        Path bashCommand=Paths.get("../../../src/main/resources/shells", "subgraph.sh");
        List<String> commands=loadshellCommands(bashCommand);
        for(String s:commands){
            executeLinuxCmd(s);
        }

        for (int k=0;k<files.size();k++){
            mappingPath = Paths.get(files.get(k));
            StringBuilder str=new StringBuilder(files.get(k));
            str.delete(0,str.lastIndexOf("/")+1);
            queryPath = Paths.get("../../../src/main/resources/pre_result/"+str.toString());
            outIniPath=Paths.get("../../../src/main/resources/ini_mapping_q20/"+str.toString());

            System.out.println("Target Graph Path: " + graphPath.toString());
            System.out.println("Query Graph Path: " + queryPath.toString());
            System.out.println("Output Path: " + outPath.toString());
            System.out.println("OutIniput Path: " + outIniPath.toString());
            System.out.println("completion type: " + type);
            System.out.println();
            PrintWriter iniWriter = new PrintWriter(outIniPath.toFile());

            ArrayList<IniGraph> graphSet = FileUtil.loadGraphSetFromFile(graphPath, "Graph ");
            ArrayList<IniGraph> querySet = FileUtil.loadGraphSetFromFile(queryPath, "Query ");
            List<List<Integer>> mapping=FileUtil.loadDataSetFromFile(mappingPath,"mapping");
            if (mapping.size()<=0){
                executeLinuxCmd("../../../../CISC/SubgraphComparing/build/matching/SubgraphMatching.out -d ../../../../CISC/SubgraphComparing/test/sample_dataset/test_case_1.graph -q ../../../src/main/resources/pre_result/"+str.toString()+" -filter DPiso -order GQL -engine LFTJ -num 100\n");
            }
            mapping=FileUtil.loadDataSetFromFile(mappingPath,"mapping");
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
//                    System.out.println("t "+i);
                    iniWriter.write("t "+i);
                    iniWriter.write("\n");
                    for (int j=0;j<stateSet.get(i).size();j++){
//                        System.out.println(stateSet.get(i).get(j)+" "+j);
                        iniWriter.write(stateSet.get(i).get(j)+" "+j);
                        iniWriter.write("\n");
                    }
                }

            }

            iniWriter.write("t # -1");
            iniWriter.flush();
            iniWriter.close();
            System.out.println("----------------finish-----------------");
        }
    }

    private static void dealCMD() {
        String start="/root/graph/new/SubgraphComparing/build/matching/SubgraphMatching.out -d " +
                "/root/graph/new/SubgraphComparing/test/sample_dataset/test_case_1.graph -q /root/graph/new/quantum_mapping_a_-/pre_result/";
        String end =" -filter GQL -order GQL -engine LFTJ -num MAX \n";

        List<String> files=FileUtil.getFiles("../../../src/main/resources/pre_result/");
        for (int k=0;k<files.size();k++){
            StringBuilder str=new StringBuilder(files.get(k));
            str.delete(0,str.lastIndexOf("/")+1);
            System.out.println(start+str.toString()+end);
        }
    }


    private static List<String> loadshellCommands(Path inpath) throws FileNotFoundException{
        List<String> result=new ArrayList<>();
        Scanner scanner = new Scanner(inpath.toFile());
        while (scanner.hasNextLine()){
            String line = scanner.nextLine().trim();
            if (line.equals("")){
                continue;
            }  else {
                result.add(line);
            }
        }

        return result;
    }

    private static void printUsage(){
        System.out.println("Usage: java -jar tabusearch.jar [connect/degree]");
    }
}
