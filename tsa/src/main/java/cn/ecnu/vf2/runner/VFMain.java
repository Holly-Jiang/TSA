package cn.ecnu.vf2.runner;

import cn.ecnu.vf2.core.IniState;
import cn.ecnu.vf2.core.MyVF2;
import cn.ecnu.vf2.graph.IniGraph;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VFMain {
        public static void main(String[] args) throws FileNotFoundException {

            Path graphPath = Paths.get("../../../src/main/resources/graphDB", "mygraphdb3.data");
            Path queryPath = Paths.get("../../../src/main/resources/graphDB", "Ex2.my");
            Path outPath = Paths.get("../../../src/main/resources/graphDB", "res_Ex2.my");
            Path outIniPath = Paths.get("../../../src/main/resources/graphDB", "ini_map.my");

            if (args.length == 0) {
                printUsage();
                System.out.println();
                System.out.println("Warning: no arguments given, using default arguments");
                System.out.println();
            }

            for (int i = 0; i < args.length; i++){
                if (args[i].equals("-t")) {
                    graphPath = Paths.get(args[i+1]);
                    i++;
                } else if (args[i].equals("-q")) {
                    queryPath = Paths.get(args[i+1]);
                    i++;
                } else if (args[i].equals("-o")) {
                    outPath = Paths.get(args[i+1]);
                    i++;
                }  else if (args[i].equals("-i")) {
                    outIniPath = Paths.get(args[i+1]);
                    i++;
                }else {
                    printUsage();
                    System.exit(1);
                }
            }

            System.out.println("Target Graph Path: " + graphPath.toString());
            System.out.println("Query Graph Path: " + queryPath.toString());
            System.out.println("Output Path: " + outPath.toString());
            System.out.println("OutIniput Path: " + outIniPath.toString());

            System.out.println();


            long startMilli = System.currentTimeMillis();

            PrintWriter writer = new PrintWriter(outPath.toFile());
            PrintWriter iniWriter = new PrintWriter(outIniPath.toFile());

            ArrayList<IniGraph> graphSet = loadGraphSetFromFile(graphPath, "Graph ");
            ArrayList<IniGraph> querySet = loadGraphSetFromFile(queryPath, "Query ");
            MyVF2 vf2= new MyVF2();

            System.out.println("Loading Done!");
            printTimeFlapse(startMilli);
            startMilli = System.currentTimeMillis();
            System.out.println();

            int queryCnt = 0;
            for (IniGraph queryGraph : querySet){
                queryCnt++;
                ArrayList<IniState> stateSet = vf2.matchGraphSetWithQuery(graphSet, queryGraph,iniWriter);
                if (stateSet.isEmpty()){
                    System.out.println("Cannot find a map for: " + queryGraph.name);
                    printTimeFlapse(startMilli);
                    printAverageMatchingTime(startMilli, queryCnt);
                    System.out.println();

                    writer.write("Cannot find a map for: " + queryGraph.name + "\n\n");
                    writer.flush();
                } else {
                    System.out.println("Found " + stateSet.size() + " maps for: " + queryGraph.name);
                    printTimeFlapse(startMilli);
                    printAverageMatchingTime(startMilli, queryCnt);
                    System.out.println();

                    writer.write("Maps for: " + queryGraph.name + "\n");
                    for (IniState state : stateSet){
                        writer.write("In: " + state.targetGraph.name + "\n");
                        //state.printMapping();
                        state.writeMapping(writer);
                    }
                    writer.write("\n");
                    writer.flush();
                }

            }

            printTimeFlapse(startMilli);
        }

        /**
         * Load graph set from file
         * @param inpath	Input path
         * @param namePrefix	The prefix of the names of graphs
         * @return	Graph SetLoading Done!
         * @throws FileNotFoundException
         */
        private static ArrayList<IniGraph> loadGraphSetFromFile(Path inpath, String namePrefix) throws FileNotFoundException{
            ArrayList<IniGraph> graphSet = new ArrayList<IniGraph>();
            Scanner scanner = new Scanner(inpath.toFile());
            IniGraph graph = null;
            while (scanner.hasNextLine()){
                String line = scanner.nextLine().trim();
                if (line.equals("")){
                    continue;
                } else if (line.startsWith("t")) {
                    String graphId = line.split(" ")[2];
                    if (graph != null){
                        graphSet.add(graph);
                    }
                    graph = new IniGraph(namePrefix + graphId);
                } else if (line.startsWith("v")) {
                    String[] lineSplit = line.split(" ");
                    int nodeId = Integer.parseInt(lineSplit[1]);
                    int nodeLabel = Integer.parseInt(lineSplit[2]);
                    graph.addNode(nodeId, nodeLabel);
                } else if (line.startsWith("e")) {
                    String[] lineSplit = line.split(" ");
                    int sourceId = Integer.parseInt(lineSplit[1]);
                    int targetId = Integer.parseInt(lineSplit[2]);
                    //int edgeLabel = Integer.parseInt(lineSplit[3]);
                    int edgeLabel = 0;
                    graph.addEdge(sourceId, targetId, edgeLabel);
                }
            }
            for(int i=0;i<graph.nodes.size();i++){
                int degree=0;
                for (int j=0;j<graph.edges.size();j++){
                    if (graph.edges.get(j).source==graph.nodes.get(i)||graph.edges.get(j).target==graph.nodes.get(i)){
                        degree++;
                    }
                }
                System.out.println(graph.nodes.get(i).id+" "+degree);

            }
            scanner.close();
            return graphSet;
        }
        private static List<List<List<Integer>>> loadDataSetFromFile(Path inpath) throws FileNotFoundException{
            List<List<List<Integer>>> result = new ArrayList<>();
            Scanner scanner = new Scanner(inpath.toFile());
            List<List<Integer>> list = new ArrayList<>();
            while (scanner.hasNextLine()){
                String line = scanner.nextLine().trim();
                if (line.equals("")){
                    continue;
                } else if (line.startsWith("t")) {
                    if (list != null&&list.size()>0){
                        result.add(list);
                    }
                    list = new ArrayList<>();
                } else {
                    String[] lineSplit = line.split(" : ");
                    int m = Integer.parseInt(lineSplit[0]);
                    int n = Integer.parseInt(lineSplit[1]);
                    List<Integer> child=new ArrayList<>();
                    child.add(m);
                    child.add(n);
                }
            }
            if (list != null&&list.size()>0){
                result.add(list);
            }
            scanner.close();
            return result;
        }
        private static void printTimeFlapse(long startMilli){
            long currentMili=System.currentTimeMillis();
            System.out.println(((currentMili - startMilli) / 1000) + " seconds elapsed");
        }

        private static void printAverageMatchingTime(long startMilli, int queryCnt){
            long currentMili=System.currentTimeMillis();
            System.out.println(((currentMili - startMilli) / queryCnt) + " milliseconds per graph in average.");
        }

        private static void printUsage(){
            System.out.println("Usage: -t target_graph_path -q query_graph_path -o output_path");
        }
    }
