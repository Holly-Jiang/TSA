package cn.ecnu.tabusearch;

import cn.ecnu.tabusearch.swaps.Gate;

import java.util.List;
import java.util.Set;

/**
 * 我的目标条件是
 * 1.当前层的门都能被执行
 * 2.深度最小 ---禁忌列表的长度稍微长一点
 * 3.SWAP数量最小 ---交换次数最少
 * 最后得到的禁忌条件是 使当前所有门距离最小并且深度最小的SWAP分值最高
 * 就选择这个门进行交换，交换完之后继续调整下一层，
 * 每一层都进行禁忌算法
 */
public class MyStopCondition implements StopCondition {
    private final Integer maxIterations;

    /**
     * Construct a {@link IterationsStopCondition}
     *
     * @param maxIterations the amount of allowed iterations
     */
    public MyStopCondition(Integer maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * Check if the current iteration is gte than the given {@code maxIterations}
     */
    @Override
    public Boolean mustStop(Integer currentIteration, MySolution bestSolutionFound) {
        if (currentIteration > maxIterations) {
//            System.out.println("超出迭代步数："+currentIteration+" > "+maxIterations);
            return true;
        }
        ShortPath[][] shortPaths = bestSolutionFound.getDist();
        List<Gate> currentLayers = bestSolutionFound.getcurrentLayers();
        for (int i=0;i<currentLayers.size();){
            Integer q1=currentLayers.get(i).getControl();
            Integer q2=currentLayers.get(i).getTarget();
            Integer l1=bestSolutionFound.locations.get(q1);
            Integer l2=bestSolutionFound.locations.get(q2);
            /**
             * 最短路径大于1说明这个2-qubits门还需要进行交换
             * 所以需要继续运行，如果当前最短路径的距离只相差1
             * 说明这条路径上的两个节点是相邻的，九八这个2-qubits门
             * 从currentLayers中移除，当currentLayers为空 就说明
             * 已经找到最好的solution，就说明当前的mapping已经满足
             * 当前的额所有2-qubits门
             *
             * 移除操作是考虑到当前swaps满足部分门还未满足所有的门
             * 避免后面的操作和之前已经满足的门产生冲突导致，所以一旦满足
             * 某个2-qubits门，就把这个门删除，就相当于这个门已经执行
             */
            Integer paths=shortPaths[l1][l2].getDistance();
            if (paths>3){
                return false;
            }else{
//                System.out.println(currentLayers.get(i).getControl() + "-----" + currentLayers.get(i).getTarget());
//                System.out.println("物理位置对应"+l1 + " - " + l2);
                bestSolutionFound.getCircuits().add(currentLayers.get(i));
                currentLayers.remove(currentLayers.get(i));
            }
        }
//        System.out.println(bestSolutionFound.swaps.size()+" ----- "+bestSolutionFound.swaps.toString());
        return true;
    }
}
