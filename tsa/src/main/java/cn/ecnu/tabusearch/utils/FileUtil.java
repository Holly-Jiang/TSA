package cn.ecnu.tabusearch.utils;

import cn.ecnu.tabusearch.ShortPath;
import cn.ecnu.tabusearch.swaps.Gate;
import cn.ecnu.tabusearch.swaps.NodeDegree;
import cn.ecnu.sc.graph.IniGraph;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUtil {

    private static Integer positions = 20;
    private static Integer nqubits = 20;

    public static void main(String[] args) throws FileNotFoundException {
        Path iniPath = Paths.get("/home/test/qubitmappingquantum_mapping_a_-/ini_mapping_q20/3_17_13");
//        read_ini(iniPath);
    }

    public static FileResult read_ini(Path inpath, ShortPath[][] dist) throws FileNotFoundException {
        FileResult result = new FileResult();
        List<Integer> lochild = new ArrayList<>();
        List<Integer> qchild = new ArrayList<>();
        if (!inpath.toFile().exists()) {
            System.out.println("read_ini文件不存在：" + inpath.getFileName());
            return result;
        }
        Scanner scanner = new Scanner(inpath.toFile());
        String line = "";
        while (scanner.hasNextLine()) {

            int Q = 0;
            int q = 0;
            line = scanner.nextLine().trim();
            if (line == "") {
                continue;
            }
            if (!line.startsWith("t")) {
                String[] arr = line.split(" ");
                Q = Integer.parseInt(arr[0]);
                q = Integer.parseInt(arr[1]);
                lochild.set(q, Q);
                qchild.set(Q, q);
            } else {
                if (lochild.size() > 0 && qchild.size() > 0) {
                    result.getQlist().add(qchild);
                    result.getLolist().add(lochild);
                }
                lochild = new ArrayList<>();
                qchild = new ArrayList<>();
                for (int i = 0; i < nqubits; i++) {
                    lochild.add(i, -1);
                    qchild.add(i, -1);
                }
            }
        }

        return result;
    }
    //生成逻辑依赖图
    public static void precessReadQasm(Path inpath, String namePrefix) throws IOException {
        List<List<Gate>> layers = new ArrayList<>();
        long ngates=0;
        long n2gates=0;
        Scanner scanner = new Scanner(inpath.toFile());
        String line = scanner.nextLine().trim();

        if (!line.equals("OPENQASM 2.0;")) {
            System.out.println("ERROR: first line of the file has to be: OPENQASM 2.0;");
            System.exit(-1);
        }

        line = scanner.nextLine().trim();
        if (!line.equals("include \"qelib1.inc\";")) {
            System.out.println("ERROR: second line of the file has to be: include \"qelib1.inc\"");
            System.exit(-1);
        }
        line = scanner.nextLine().trim();
        int n = -1;
        if (!line.startsWith("qreg")) {
            System.out.println("ERROR: failed to parse qasm file: " + line);
            System.exit(-1);
        }
        n = Integer.parseInt(line.substring(7, line.length() - 2));
        if (n > positions) {
            System.out.println("ERROR: too many qubits for target architecture: " + n);
            System.exit(-1);
        }

        line = scanner.nextLine().trim();
        if (!line.startsWith("creg")) {
            System.out.println("ERROR: failed to parse qasm file: " + line);
            System.exit(-1);
        }
        List<Integer> last_layer = new ArrayList<>(20);
        for (int i = 0; i < positions; i++) {
            last_layer.add(-1);
        }

        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            if (line.equals("")) {
                continue;
            }
            Gate g = new Gate();
            int layer = 0;
            String[] str = line.split(" |,");
            if (str.length == 3) {
                g.setType(str[0]);
                g.setControl(Integer.parseInt(str[1].substring(2, str[1].length() - 1)));
                g.setTarget(Integer.parseInt(str[2].substring(2, str[2].length() - 2)));
                layer = Math.max(last_layer.get(g.getTarget()), last_layer.get(g.getControl())) + 1;
                last_layer.set(g.getTarget(), layer);
                last_layer.set(g.getControl(), layer);
                n2gates++;
            } else if (str.length == 2) {
                if (str[0].startsWith("rz")) {
                    double angle;
                    angle = Double.parseDouble(str[0].substring(3, str[0].length() - 1));
                    g.setControl(-1);
                    g.setTarget(Integer.parseInt(str[1].substring(2, str[1].length() - 2)));
                    g.setType("rz");
                    g.setAngle(angle);
                } else {
                    g.setType(str[0]);
                    g.setControl(-1);
                    g.setTarget(Integer.parseInt(str[1].substring(2, str[1].length() - 2)));
                    layer = last_layer.get(g.getTarget()) + 1;
                    last_layer.set(g.getTarget(), layer);
                }

            } else {
                System.out.println("ERROR: could not read gate: " + line);
                System.exit(-1);
            }
            ngates++;

            if (layers.size() <= layer) {
                layers.add(new ArrayList<Gate>());
            }
            layers.get(layer).add(g);
        }

        Set<Integer> list = new HashSet<>();
        List<List<Integer>> results = new ArrayList<>();
        int max_node = -1;//最大的节点id
        System.out.println("---------*************-----------");
        Map<String, Integer> layer_map = new HashMap<>();
        for (int i = 0; i < layers.size(); i++) {
            for (int j = 0; j < layers.get(i).size(); j++) {
                //cout<<layers[i][j].control<<"-"<<layers[i][j].target<<":"<<layer_map.count(to_string(layers[i][j].control)+"-"+to_string(layers[i][j].target))<<endl;
                if (layers.get(i).get(j).getControl() != -1 && !layer_map.containsKey(layers.get(i).get(j).getControl() + "-" + layers.get(i).get(j).getTarget())
                        && !layer_map.containsKey(layers.get(i).get(j).getTarget() + "-" + layers.get(i).get(j).getControl())) {

                    if (max_node < layers.get(i).get(j).getControl()) {
                        max_node = layers.get(i).get(j).getControl();
                    }
                    if (max_node < layers.get(i).get(j).getTarget()) {
                        max_node = layers.get(i).get(j).getTarget();
                    }
                    List<Integer> temp = new ArrayList<>();
                    temp.add(layers.get(i).get(j).getControl());
                    temp.add(layers.get(i).get(j).getTarget());
                    results.add(temp);
                    layer_map.put(layers.get(i).get(j).getTarget() + "-" + layers.get(i).get(j).getControl(), 1);
                }
            }
        }
        for (int i = 0; i < max_node + 1; i++) {
            list.add(i);
        }

        Path outPath = Paths.get("../../../src/main/resources/results/", "initial_information");
        FileWriter writer = new FileWriter(outPath.toFile(),true);
        StringBuilder pre_str = new StringBuilder();
        pre_str.append("../../../src/main/resources/pre_result/");
        pre_str.append(namePrefix);
        Path pre_path = Paths.get(pre_str.toString());
        PrintWriter pre_out = new PrintWriter(pre_path.toFile());
        writer.append(namePrefix+"\n");
        writer.append( (max_node + 1) + " " + n2gates+ "\n");
        pre_out.write("t " + (max_node + 1) + " " + results.size() + "\n");
        System.out.println("t " + (max_node + 1) + " " + results.size());
        Iterator<Integer> it1 = list.iterator();
        //分別存放节点编号和节点的度数,节点编号唯一
        List<NodeDegree> nds=new ArrayList<>();
        while (it1.hasNext()) {
            int degree = 0;
            Integer node = it1.next();
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).get(0) == node || results.get(i).get(1) == node) {
                    degree++;
                }
            }
            NodeDegree nd=new NodeDegree();
            nd.setNodeId(node);
            nd.setDegree(degree);
            nds.add(nd);
        }
        Collections.sort(nds);
        for (int i=0;i<nds.size();i++){
            if (nds.get(i).getDegree()>0){
                break;
            }else{
                //手动添加边 连接性为1
                NodeDegree nd1=nds.get(i);
                nd1.setDegree(nd1.getDegree()+1);
                NodeDegree nd=nds.get(nds.size()-1);
                nd.setDegree(nd.getDegree()+1);
                List<Integer> temp = new ArrayList<>();
                temp.add(nd1.getNodeId());
                temp.add(nd.getNodeId()) ;
                results.add(temp);
            }
        }
        //必须要按节点Id大小输出才能进行子图匹配
        Collections.sort(nds, new Comparator<NodeDegree>() {
            @Override
            public int compare(NodeDegree nodeDegree, NodeDegree t1) {
                return nodeDegree.getNodeId()-t1.getNodeId();
            }
        });
        for (int i=0;i<nds.size();i++){
            pre_out.write("v " + nds.get(i).getNodeId() + " " + 0 + " " + nds.get(i).getDegree() + "\n");
            System.out.println("v " + nds.get(i).getNodeId() + " " + 0 + " " + nds.get(i).getDegree());

        }
        for (int i = 0; i < results.size(); i++) {
            pre_out.write("e " + results.get(i).get(0) + " " + results.get(i).get(1) + "\n");
            System.out.println("e " + results.get(i).get(0) + " " + results.get(i).get(1));
        }
        pre_out.write("t # -1 \n");
        System.out.println("t # -1 ");
        System.out.println("----------*******************----------");
        pre_out.close();
        writer.close();
    }
    public static FileResult read_qasm(Path inpath, String namePrefix) throws FileNotFoundException {
        List<List<Gate>> layers = new ArrayList<>();
        long ngates=0;
        long n2gates=0;
        Scanner scanner = new Scanner(inpath.toFile());
        String line = scanner.nextLine().trim();
        FileResult result=new FileResult();
        result.setLayers(layers);

        if (!line.equals("OPENQASM 2.0;")) {
            System.out.println("ERROR: first line of the file has to be: OPENQASM 2.0;");
            System.exit(-1);
        }

        line = scanner.nextLine().trim();
        if (!line.equals("include \"qelib1.inc\";")) {
            System.out.println("ERROR: second line of the file has to be: include \"qelib1.inc\"");
            System.exit(-1);
        }
        line = scanner.nextLine().trim();
        int n = -1;
        if (!line.startsWith("qreg")) {
            System.out.println("ERROR: failed to parse qasm file: " + line);
            System.exit(-1);
        }
        n = Integer.parseInt(line.substring(7, line.length() - 2));
        if (n > positions) {
            System.out.println("ERROR: too many qubits for target architecture: " + n);
            System.exit(-1);
        }

        line = scanner.nextLine().trim();
        if (!line.startsWith("creg")) {
            System.out.println("ERROR: failed to parse qasm file: " + line);
            System.exit(-1);
        }
        List<Integer> last_layer = new ArrayList<>(20);
        for (int i = 0; i < positions; i++) {
            last_layer.add(-1);
        }

        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            if (line.equals("")) {
                continue;
            }
            Gate g = new Gate();
            int layer = 0;
            String[] str = line.split(" |,");
            if (str.length == 3) {
                g.setType(str[0]);
                g.setControl(Integer.parseInt(str[1].substring(2, str[1].length() - 1)));
                g.setTarget(Integer.parseInt(str[2].substring(2, str[2].length() - 2)));
                layer = Math.max(last_layer.get(g.getTarget()), last_layer.get(g.getControl())) + 1;
                last_layer.set(g.getTarget(), layer);
                last_layer.set(g.getControl(), layer);
                n2gates++;
            } else if (str.length == 2) {
                if (str[0].startsWith("rz")) {
                    double angle;
                    angle = Double.parseDouble(str[0].substring(3, str[0].length() - 1));
                    g.setControl(-1);
                    g.setTarget(Integer.parseInt(str[1].substring(2, str[1].length() - 2)));
                    g.setType("rz");
                    g.setAngle(angle);
                } else {
                    g.setType(str[0]);
                    g.setControl(-1);
                    g.setTarget(Integer.parseInt(str[1].substring(2, str[1].length() - 2)));
                    layer = last_layer.get(g.getTarget()) + 1;
                    last_layer.set(g.getTarget(), layer);
                }

            } else {
                System.out.println("ERROR: could not read gate: " + line);
                System.exit(-1);
            }
             ngates++;

            if (layers.size() <= layer) {
                layers.add(new ArrayList<Gate>());
            }
            layers.get(layer).add(g);
        }
        result.setNgates(ngates);
        result.setN2gates(n2gates);
        return result;
    }
    public static ArrayList<String> getFiles(String filepath){
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(filepath);
        File[] tempLists = file.listFiles();
        for (int i = 0; i < tempLists.length; i ++) {
            if (tempLists[i].isFile()) {
                files.add(tempLists[i].toString());
            }
        }
        return files;
    }
    public static ArrayList<IniGraph> loadGraphSetFromFile(Path inpath, String namePrefix) throws FileNotFoundException{
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
                int nodedegree = Integer.parseInt(lineSplit[3]);
                graph.addNode(nodeId, nodedegree);
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
    public static List<List<Integer>> loadDataSetFromFile(Path inpath, String namePrefix) throws FileNotFoundException{
        List<List<Integer>> result = new ArrayList<>();
        Scanner scanner = new Scanner(inpath.toFile());
        List<Integer> list = new ArrayList<>();
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
                int n = Integer.parseInt(lineSplit[1]);
                list.add(n);

            }
        }
        if (list != null&&list.size()>0){
            result.add(list);
        }
        scanner.close();
        return result;
    }
    public static  FileResult readFYIni(Path inpath) throws FileNotFoundException {
        FileResult result = new FileResult();
        if (!inpath.toFile().exists()) {
            System.out.println("readFYIni文件不存在：" + inpath.getFileName());
            return result;
        }
        Boolean flag=false;
        Scanner scanner = new Scanner(inpath.toFile());
        while (scanner.hasNextLine()) {
            List<Integer> lochild = new ArrayList<>();
            List<Integer> qchild = new ArrayList<>();
            result.getQlist().add(qchild);
            result.getLolist().add(lochild);
            String line = scanner.nextLine().trim();
            if (line.equals("")||line.length()<1){
                continue;
            }
            String line1=line.substring(1,line.length()-1);
            String [] str=line1.split(",");
            for (int i=0;i<str.length;i++){
                qchild.add(Integer.parseInt(str[i].trim()));
                lochild.add(-1);
            }
            for (int i = 0; i < positions; i++)
            {
                flag=true;
                if(qchild.get(i).equals(20)){
                    qchild.set(i,-1);
                }else{
                    lochild.set(qchild.get(i),i);
                }
            }

            if(!flag){
                System.out.println("initial mapping fail!");
                System.exit(4);
            }
        }
        return result;

    }
    public static FileResult read_qasm_to_compute_depth(Path inpath) throws FileNotFoundException {
        List<List<Gate>> layers = new ArrayList<>();
        long n2gates=0;
        long ngates=0;
        Scanner scanner = new Scanner(inpath.toFile());
        String line = scanner.nextLine().trim();
        FileResult result=new FileResult();
        result.setLayers(layers);

        if (!line.equals("OPENQASM 2.0;")) {
            System.out.println("ERROR: first line of the file has to be: OPENQASM 2.0;");
            System.exit(-1);
        }

        line = scanner.nextLine().trim();
        if (!line.equals("include \"qelib1.inc\";")) {
            System.out.println("ERROR: second line of the file has to be: include \"qelib1.inc\"");
            System.exit(-1);
        }
        line = scanner.nextLine().trim();
        int n = -1;
        if (!line.startsWith("qreg")) {
            System.out.println("ERROR: failed to parse qasm file: " + line);
            System.exit(-1);
        }
        n = Integer.parseInt(line.substring(7, line.length() - 2));
        if (n > positions) {
            System.out.println("ERROR: too many qubits for target architecture: " + n);
            System.exit(-1);
        }

        line = scanner.nextLine().trim();
        if (!line.startsWith("creg")) {
            System.out.println("ERROR: failed to parse qasm file: " + line);
            System.exit(-1);
        }
        List<Integer> last_layer = new ArrayList<>(20);
        for (int i = 0; i < positions; i++) {
            last_layer.add(-1);
        }

        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            if (line.equals("")) {
                continue;
            }
            Gate g = new Gate();
            int layer = 0;
            String[] str = line.split(" |,");
            if (str.length == 3) {
                g.setType(str[0]);
                g.setControl(Integer.parseInt(str[1].substring(2, str[1].length() - 1)));
                g.setTarget(Integer.parseInt(str[2].substring(2, str[2].length() - 2)));
                layer = Math.max(last_layer.get(g.getTarget()), last_layer.get(g.getControl())) + 1;
                last_layer.set(g.getTarget(), layer);
                last_layer.set(g.getControl(), layer);
                n2gates++;
            } else if (str.length == 2) {
                if (str[0].startsWith("rz")) {
                    double angle;
                    angle = Double.parseDouble(str[0].substring(3, str[0].length() - 1));
                    g.setControl(-1);
                    g.setTarget(Integer.parseInt(str[1].substring(2, str[1].length() - 2)));
                    g.setType("rz");
                    g.setAngle(angle);
                } else {
                    g.setType(str[0]);
                    g.setControl(-1);
                    g.setTarget(Integer.parseInt(str[1].substring(2, str[1].length() - 2)));
                    layer = last_layer.get(g.getTarget()) + 1;
                    last_layer.set(g.getTarget(), layer);
                }

            } else {
                System.out.println("ERROR: could not read gate: " + line);
                System.exit(-1);
            }
            ngates++;

            if (layers.size() <= layer) {
                layers.add(new ArrayList<Gate>());
            }
            layers.get(layer).add(g);
        }
//        System.out.println("---------*************-----------");
        nqubits = positions;
        result.setN2gates(n2gates);
        result.setNgates(ngates);
        return result;
    }
    public static FileResult compute_depth(List<Gate > currentLayer) throws FileNotFoundException {
        List<List<Gate>> layers = new ArrayList<>();
        long ngates=0;
        FileResult result=new FileResult();
        result.setLayers(layers);

        List<Integer> last_layer = new ArrayList<>(20);
        for (int i = 0; i < positions; i++) {
            last_layer.add(-1);
        }

       for (int i=0;i< currentLayer.size();i++){

            Gate g = currentLayer.get(i);
            int layer = 0;
            if (g.getControl()!=-1) {
                layer = Math.max(last_layer.get(g.getTarget()), last_layer.get(g.getControl())) + 1;
                last_layer.set(g.getTarget(), layer);
                last_layer.set(g.getControl(), layer);
            } else if (g.getControl()+1==0) {
                    layer = last_layer.get(g.getTarget()) + 1;
                    last_layer.set(g.getTarget(), layer);
            } else {
                System.out.println("ERROR: could not read gate: " + currentLayer.get(i));
                System.exit(-1);
            }
            ngates++;

            if (layers.size() <= layer) {
                layers.add(new ArrayList<Gate>());
            }
            layers.get(layer).add(g);
        }
        nqubits = positions;
        result.setNgates(ngates);
        return result;
    }
    public static void generate_VF_formalization(Path inpath, String name) throws IOException {
        Scanner scanner = new Scanner(inpath.toFile());
        String line = scanner.nextLine().trim();

        if (!line.equals("OPENQASM 2.0;")) {
            System.out.println("ERROR: first line of the file has to be: OPENQASM 2.0;");
            System.exit(-1);
        }

        line = scanner.nextLine().trim();
        if (!line.equals("include \"qelib1.inc\";")) {
            System.out.println("ERROR: second line of the file has to be: include \"qelib1.inc\"");
            System.exit(-1);
        }
        line = scanner.nextLine().trim();
        int n = -1;
        if (!line.startsWith("qreg")) {
            System.out.println("ERROR: failed to parse qasm file: " + line);
            System.exit(-1);
        }
        n = Integer.parseInt(line.substring(7, line.length() - 2));
        if (n > positions) {
            System.out.println("ERROR: too many qubits for target architecture: " + n);
            System.exit(-1);
        }

        line = scanner.nextLine().trim();
        if (!line.startsWith("creg")) {
            System.out.println("ERROR: failed to parse qasm file: " + line);
            System.exit(-1);
        }
        List<Integer> last_layer = new ArrayList<>(20);
        for (int i = 0; i < positions; i++) {
            last_layer.add(-1);
        }
        List<String> result=new ArrayList<>();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            if (line.equals("")) {
                continue;
            }
            String[] str = line.split(" |,");
            if (str.length == 3) {
                StringBuilder sb=new StringBuilder();
                sb.append("[");
                sb.append(Integer.parseInt(str[1].substring(2, str[1].length() - 1)));
                sb.append(", ");
                sb.append(Integer.parseInt(str[2].substring(2, str[2].length() - 2)));
                sb.append("]");
                result.add(sb.toString());
            }
        }
        Path outPath = Paths.get("../../../src/main/resources/VF_formalization", name);
        FileWriter iniWriter = new FileWriter(outPath.toFile(), false);
        iniWriter.write("[");
        for (int i=0;i<result.size();i++){
            iniWriter.write(result.get(i));
            if (i==result.size()-1){
                continue;
            }
            iniWriter.write(", ");
        }
        iniWriter.write("]");
        iniWriter.flush();
        iniWriter.close();
    }
}
