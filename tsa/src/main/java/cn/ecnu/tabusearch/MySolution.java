package cn.ecnu.tabusearch;

import cn.ecnu.tabusearch.swaps.Gate;
import cn.ecnu.tabusearch.utils.FileResult;
import cn.ecnu.tabusearch.utils.FileUtil;
import cn.ecnu.tabusearch.utils.GraphUtil;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MySolution implements  Solution {
    public MySolution(Set<Edge> graph,ShortPath [][] dist, List<Integer> locations, List<Integer> qubits, List<Gate> currentLayers, List<Gate> nextLayers_1) {
        this.dist = dist;
        this.locations = new ArrayList<>(locations);
        this.qubits = new ArrayList<>(qubits);
        this.currentLayers=new ArrayList<>(currentLayers);
        this.nextLayers_1=nextLayers_1;
        this.graph=graph;
    }
    Set<Edge> graph;
    //所有门的最终执行顺序
    List<Gate> circuits=new ArrayList<>();
    List<Edge> swaps=new ArrayList<>();
    //这个交换的得分
    Double value;
    //可能是下一个待交换列表
    List<Solution> neighbors;
    //当前层需要满足相邻关系的门
    List<Gate> currentLayers;
    //前瞻层 权重0.7
    List<Gate> nextLayers_1;
    //距离矩阵
    ShortPath [][]  dist;
    //还需要一个物理结构的距离矩阵和当前映射
    //index表示逻辑qubit locations[index]表示逻辑qubit映射的物理qubit位置,-1表示没有进行映射
    List<Integer> locations;
    //index表示物理qubit，qubits[index]表示物理qubit映射的哪个逻辑qubit ，-1表示没有进行映射
    List<Integer> qubits;

    public List<Gate> getCircuits() {
        return circuits;
    }

    public void setCircuits(List<Gate> circuits) {
        this.circuits = circuits;
    }

    public List<Gate> getCurrentLayers() {
        return currentLayers;
    }

    public void setCurrentLayers(List<Gate> currentLayers) {
        this.currentLayers = currentLayers;
    }

    public List<Gate> getNextLayers_1() {
        return nextLayers_1;
    }

    public void setNextLayers_1(List<Gate> nextLayers_1) {
        this.nextLayers_1 = nextLayers_1;
    }

    public List<Edge> getSwaps() {
        return swaps;
    }

    public void setSwaps(List<Edge> swaps) {
        this.swaps = swaps;
    }
    public ShortPath [][] getDist() {
        return dist;
    }

    public void setDist(ShortPath [][] dist) {
        this.dist = dist;
    }

    public List<Integer> getLocations() {
        return locations;
    }

    public void setLocations(List<Integer> locations) {
        this.locations = locations;
    }

    public List<Integer> getQubits() {
        return qubits;
    }

    public void setQubits(List<Integer> qubits) {
        this.qubits = qubits;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setNeighbors(List<Solution> neighbors) {
        this.neighbors = neighbors;
    }

    public List<Gate> getcurrentLayers() {
        return currentLayers;
    }

    public void setcurrentLayers(List<Gate> currentLayers) {
        this.currentLayers = currentLayers;
    }

    /**
     * 分数的计算方式就是更新完这个SWAP之后的临时映射的得分 能使得当前层所有门的距离是多少 还有就是深度
     * 计算当前层的门的最短路径得分，每条边的分值为1
     * @return
     */
    @Override
    public Double getValue() {

        return this.value;
    }
    @Override
    public List<Solution> getNeighbors(int type) {
        NeighborResult result=computeNeighbor(graph,this,dist,qubits,locations,currentLayers,nextLayers_1,type);
       this.neighbors= result.getSolutions();
       return this.neighbors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MySolution that = (MySolution) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(neighbors, that.neighbors);
    }
    public static NeighborResult computeNeighbor(Set<Edge> graph,Solution parent,ShortPath [][] dist,List<Integer> qubits,
                                                  List<Integer> locations,List<Gate> currentLayers1,List<Gate> nextLayers_1,int type) {
        List<Gate> curr_solved_gates=new ArrayList<>();
        NeighborResult result=new NeighborResult();
        List<Solution> solutions=new ArrayList<>();
        result.setSolutions(solutions);
        MySolution p=(MySolution)parent;
        result.setCurr_solved_gates(curr_solved_gates);
        Set<Integer> include_nodes=new HashSet<>();
        List<Gate> currentLayers=new ArrayList<>(currentLayers1);
        /**
         * 计算邻域
         * 遍历初始给定的门关系
         * 然后计算几对门的最短路径，每条路径中的每一步都是一次候选交换
         * 顺便计算每条边的权重
         */
        for (int i=0;i<currentLayers.size();i++) {
            Integer loc1 = locations.get(currentLayers.get(i).getControl());
            Integer loc2 = locations.get(currentLayers.get(i).getTarget());
            ShortPath distance = dist[loc1][loc2];
            //The path length exceeds 3 to exchange only for Q20
            if (distance.getDistance() <= 3) {
                //相邻的门可以先执行，以免后续操作影响
                curr_solved_gates.add(currentLayers.get(i));
                currentLayers.remove(currentLayers.get(i));
                i--;
                continue;
            }else{
                include_nodes.add(loc1);
                include_nodes.add(loc2);
            }
        }
        for (int i=0;i<currentLayers.size();i++){
            Integer loc1=locations.get(currentLayers.get(i).getControl());
            Integer loc2=locations.get(currentLayers.get(i).getTarget());
            ShortPath distance=dist[loc1][loc2];
            List<List<Edge>> paths=distance.getPaths();

                /**
                 * 当前路径的得分
                 * 选择当前路径和之前路径重合度最高的路径
                 * todo 只能选择和之前已选择的边对比  后面出现的无法对比
                 * 这里就涉及到边的权重问题了
                 * 如果仅仅对比重合度 不能体现look-ahead的边的表现
                 * 所以改进就是要计算每条边的权重 look-ahead的权重0.7=x*7/10
                 * 再想想 应该是 领域仅包含currentLayer的路径 只是计算每个solution的value可以把下一层的路径考虑进来
                 */

            for(int k=0;k<paths.size();k++){
                for (int j=0;j<paths.get(k).size();j++) {
                    //生成临时交换j 和j+1
                    Integer sour_node = paths.get(k).get(j).source;
                    Integer tar_node = paths.get(k).get(j).target;
                    //至少和节点有一个交集的边的交换才有意义
                    if (include_nodes.contains(sour_node) || include_nodes.contains(tar_node)) {
                        List<Integer> newQubits = new ArrayList<>(qubits);
                        List<Integer> newLocations = new ArrayList<>(locations);
                        Integer q1 = newQubits.get(paths.get(k).get(j).source);
                        Integer q2 = newQubits.get(paths.get(k).get(j).target);
                        newQubits.set(paths.get(k).get(j).source, q2);
                        newQubits.set(paths.get(k).get(j).target, q1);
                        if (q1 != -1) {
                            newLocations.set(q1, paths.get(k).get(j).target);
                        }
                        if (q2 != -1) {
                            newLocations.set(q2, paths.get(k).get(j).source);
                        }
                        MySolution s = new MySolution(graph, dist, newLocations, newQubits, currentLayers, nextLayers_1);
                        s.neighbors = new LinkedList<>();
                        s.swaps.addAll(p.swaps);
                        s.getCircuits().addAll(p.getCircuits());
                        s.getCircuits().addAll(curr_solved_gates);
                        s.swaps.add(paths.get(k).get(j));
                        addQX20SwapGates(paths.get(k).get(j), graph, s.getCircuits());
//                    计算当前的value
                        if(type==0){
                            s.value = computeValue(dist, newLocations, currentLayers, nextLayers_1);
                        }else{
                            try{
                                s.value=computeDepthValue(currentLayers,nextLayers_1);
                            }catch(FileNotFoundException e){
                                e.printStackTrace();
                            }
                        }


                        solutions.add(s);
                    }
                }
            }
        }

//        }
        result.setCurrent_num(currentLayers1.size());
        return result;
    }

    public static NeighborResult computeNeighbor1(Set<Edge> graph,Solution parent,ShortPath [][] dist,List<Integer> qubits,
                                                 List<Integer> locations,List<Gate> currentLayers1,List<Gate> nextLayers_1) {
        List<Gate> curr_solved_gates=new ArrayList<>();
        NeighborResult result=new NeighborResult();
        List<Solution> solutions=new ArrayList<>();
        List<List<Edge>> choose_path=new ArrayList<>();
        result.setChoose_path(choose_path);
        result.setSolutions(solutions);
        MySolution p=(MySolution)parent;
        result.setCurr_solved_gates(curr_solved_gates);
        Set<Edge> choosed_edges=new HashSet<>();
        List<Gate> currentLayers=new ArrayList<>(currentLayers1);
        /**
         * 计算邻域
         * 遍历初始给定的门关系
         * 然后计算几对门的最短路径，每条路径中的每一步都是一次候选交换
         * 顺便计算每条边的权重
         */
        for (int i=0;i<currentLayers.size();i++) {
            Integer loc1 = locations.get(currentLayers.get(i).getControl());
            Integer loc2 = locations.get(currentLayers.get(i).getTarget());
            ShortPath distance = dist[loc1][loc2];
            //The path length exceeds 3 to exchange only for Q20
            if (distance.getDistance() <= 3) {
//                System.out.println(currentLayers.get(i).getControl() + "-----" + currentLayers.get(i).getTarget());
//                System.out.println(loc1 + " - " + loc2 + " = " + distance.getDistance());
                //相邻的门可以先执行，以免后续操作影响
                curr_solved_gates.add(currentLayers.get(i));
                currentLayers.remove(currentLayers.get(i));
                i--;
                continue;
            }
            //將所有路径的边全部加入choosed_edges 后续选择某条边时，选择权重最大的路径，说明该条路径重合度最高
          for (int j=0;j<distance.getPaths().size();j++){
              for (Edge e:distance.getPaths().get(j)){
                  if (choosed_edges.contains(e)){
                      e.weight+=1;
                  }else{
                      e.weight=1;
                  }
                  choosed_edges.add(e);
              }
          }
        }
        for (int i=0;i<currentLayers.size();i++){
            Integer loc1=locations.get(currentLayers.get(i).getControl());
            Integer loc2=locations.get(currentLayers.get(i).getTarget());
            ShortPath distance=dist[loc1][loc2];
            List<List<Edge>> paths=distance.getPaths();
            //找到和当前最重合的路径
            int max_cout=0,sum=0,path_index=-1;
            for (int k=0;k<paths.size();k++){
                /**
                 * 当前路径的得分
                 * 选择当前路径和之前路径重合度最高的路径
                 * todo 只能选择和之前已选择的边对比  后面出现的无法对比
                 * 这里就涉及到边的权重问题了
                 * 如果仅仅对比重合度 不能体现look-ahead的边的表现
                 * 所以改进就是要计算每条边的权重 look-ahead的权重0.7=x*7/10
                 * 再想想 应该是 领域仅包含currentLayer的路径 只是计算每个solution的value可以把下一层的路径考虑进来
                 */
                sum=0;
                for (int j=0;j<paths.get(k).size();j++){
                    if (choosed_edges.contains(paths.get(k).get(j))){
                        Iterator<Edge>it= choosed_edges.iterator();
                        while (it.hasNext()){
                            Edge ee=it.next();
                            if (ee.equals(paths.get(k).get(j))){
                                //计算权重最高的路径
                                sum+=ee.weight;
                            }
                        }
                    }
                }

                if (max_cout<sum){
                    max_cout=sum;
                    path_index=k;
                }
            }
            if (path_index>=0){
                choose_path.add(new ArrayList<>(paths.get(path_index)));
                for (int j=0;j<paths.get(path_index).size();j++){
                    //生成临时交换j 和j+1
                    List<Integer> newQubits=new ArrayList<>(qubits);
                    List<Integer> newLocations=new ArrayList<>(locations);
                    Integer q1=newQubits.get(paths.get(path_index).get(j).source);
                    Integer q2=newQubits.get(paths.get(path_index).get(j).target);
                    newQubits.set(paths.get(path_index).get(j).source,q2);
                    newQubits.set(paths.get(path_index).get(j).target,q1);
                    if (q1!=-1){
                        newLocations.set(q1,paths.get(path_index).get(j).target);
                    }
                    if (q2!=-1){
                        newLocations.set(q2,paths.get(path_index).get(j).source);
                    }
                    MySolution s=new MySolution(graph,dist,newLocations,newQubits,currentLayers,nextLayers_1);
                    s.neighbors = new LinkedList<>();
                    s.swaps.addAll(p.swaps);
                    s.getCircuits().addAll(p.getCircuits());
                    s.getCircuits().addAll(curr_solved_gates);
                    s.swaps.add(paths.get(path_index).get(j));
                    addQX20SwapGates(paths.get(path_index).get(j),graph,s.getCircuits());
//                    计算当前的value
                    s.value=computeValue(dist,newLocations,currentLayers,nextLayers_1);
//                       try{
//                    s.value=computeDepthValue(currentLayers,nextLayers_1);
//                }catch(FileNotFoundException e){
//                    e.printStackTrace();
//                }

//                    System.out.println("new locations: "+newLocations+" value: "+s.value);
                    solutions.add(s);
                }
            }

        }
        result.setCurrent_num(currentLayers1.size());
        return result;
    }
    public static  void addQX20SwapGates(Edge e,Set<Edge> graph,List<Gate> circuits){
        Gate cnot=new Gate();
        cnot.setType("cx");
        Gate cnot2=new Gate();
        cnot2.setType("cx");
        cnot.setControl(e.source);
        cnot.setTarget(e.target);
        cnot2.setControl(e.target);
        cnot2.setTarget(e.source);
        Gate gg=new Gate();
        gg.setControl(cnot.getControl());
        gg.setTarget(cnot.getTarget());
        gg.setType("SWP");
        circuits.add(cnot);
        circuits.add(cnot2);
        circuits.add(cnot);
        //Insert a dummy SWAP gate to allow for tracking the positions of the logical qubits
//        circuits.add(gg);
    }
    public static  void addSwapGates(Edge e,Set<Edge> graph,List<Gate> circuits){
        Gate cnot=new Gate();
        cnot.setType("cx");
        Gate h1=new Gate();
        h1.setType("h");
        Gate h2=new Gate();
        h2.setType("h");
        if (GraphUtil.contains(graph,e)) {
            cnot.setControl(e.source);
            cnot.setTarget(e.target);
        } else {
            cnot.setControl(e.target);
            cnot.setTarget(e.source);

            int tmp = e.source;
            e.source = e.target;
            e.target = tmp;
            if (!GraphUtil.contains(graph,e)) {
                System.out.println("ERROR: invalid SWAP gate");
                System.exit(-2);
            }
        }
        h1.setControl(-1);
        h2.setControl(-1);
        h1.setTarget(e.source);
        h2.setTarget(e.target);
        Gate gg=new Gate();
        gg.setControl(cnot.getControl());
        gg.setTarget(cnot.getTarget());
        gg.setType("SWP");
        circuits.add(cnot);
        circuits.add(h1);
        circuits.add(h2);
        circuits.add(cnot);
        circuits.add(h1);
        circuits.add(h2);
        circuits.add(cnot);
        //Insert a dummy SWAP gate to allow for tracking the positions of the logical qubits
//        circuits.add(gg);
    }
    public static Double computeValue(ShortPath [][] dist ,List<Integer> locations,List<Gate> currentLayers,List<Gate> nextLayers_1){
        Double result=0.0;
        for (int i=0;i<currentLayers.size();i++){
            Integer loc1=locations.get(currentLayers.get(i).getControl());
            Integer loc2=locations.get(currentLayers.get(i).getTarget());
            ShortPath distance=dist[loc1][loc2];
            result+=distance.getDistance();
        }
        //前瞻层每个CNOT门之间的距离 为了控制前瞻层在控制力度，设置权重0.7
        if (!nextLayers_1.isEmpty()){
            for (int i=0;i<nextLayers_1.size();i++){
                Integer loc1=locations.get(nextLayers_1.get(i).getControl());
                Integer loc2=locations.get(nextLayers_1.get(i).getTarget());
                ShortPath distance=dist[loc1][loc2];
//                result+=distance.getDistance();
                result+=(distance.getDistance()*9)/10;
            }
        }
        return result;
    }
    //优先考虑电路深度小
    public static Double computeDepthValue(List<Gate> currentLayers,List<Gate> nextLayers_1) throws FileNotFoundException {
        FileResult depthFile=FileUtil.compute_depth(currentLayers);
        //前瞻层每个CNOT门之间的距离 为了控制前瞻层在控制力度，设置权重0.7
        FileResult nextdepthFile=FileUtil.compute_depth(nextLayers_1);

        return depthFile.getLayers().size()+(nextdepthFile.getLayers().size()+0.0)*9/10;
//        return  depthFile.getLayers().size()+0.0;
    }
    public static void computeWeight(List<List<Edge>> choose_path,Set<Edge> graph){
        Iterator<Edge> it=graph.iterator();
        while (it.hasNext()){
            Edge e=it.next();
            for (int i=0;i<choose_path.size();i++){
                if (choose_path.get(i).contains(e)){
                    e.weight++;
                }else {
                    int tmp=e.source;
                    e.source=e.target;
                    e.target=tmp;
                    if (choose_path.get(i).contains(e)){
                        e.weight++;
                    }
                }
            }
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(value, neighbors);
    }

    @Override
    public String toString() {
        return "MySolution{" +
                "value=" + value +
                ", neighbors=" + neighbors +
                '}';
    }

}
