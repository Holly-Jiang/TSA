package cn.ecnu.tabusearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;

/**
 * Default implementation of the Tabu Search algorithm
 *
 * @author Alex Ferreira
 */
public class TabuSearch {

    private TabuList tabuList;
    private StopCondition stopCondition;
    private MyNeighborSolutionLocator solutionLocator;

    /**
     * Construct a {@link TabuSearch} object
     *
     * @param tabuList        the tabu list used in the algorithm to handle tabus
     * @param stopCondition   the algorithm stop condition
     * @param solutionLocator the best neightbor solution locator to be used in each algortithm iteration
     */
    public TabuSearch(TabuList tabuList, StopCondition stopCondition, MyNeighborSolutionLocator solutionLocator) {
        this.tabuList = tabuList;
        this.stopCondition = stopCondition;
        this.solutionLocator = solutionLocator;
    }

    /**
     * Execute the algorithm to perform a minimization.
     *
     * @param initialSolution the start point of the algorithm
     * @return the best solution found in the given conditions
     */
    public Solution run(Solution initialSolution,int type) {
        MySolution bestSolution = (MySolution) initialSolution;
        MySolution currentSolution = (MySolution) initialSolution;

        Integer currentIteration = 0;
        while (!stopCondition.mustStop(++currentIteration, bestSolution)) {

            List<Solution> candidateNeighbors = new ArrayList<>(currentSolution.getNeighbors(type));
            List<Solution> solutionsInTabu = IteratorUtils.toList(tabuList.iterator());

            Solution bestNeighborFound = solutionLocator.findBestNeighbor(candidateNeighbors, solutionsInTabu);
            //特赦规则
            if (bestNeighborFound == null) {
                //没有候选集
                if (currentSolution.getNeighbors(type) == null || currentSolution.getNeighbors(type).size() <= 0) {
//                    System.out.println("没有候选集");
                    break;
                } else {
                    //在候选集中选择一个最好的领域成员
                    /** 选择规则
                     *选择邻域中value最小的一个Solution作为bestNeighbor
                     */
                    bestNeighborFound = solutionLocator.findAmnestyNeighbor(currentSolution.getNeighbors(type), solutionsInTabu);
                }
            }
            bestSolution = (MySolution) bestNeighborFound;
//            System.out.println(currentIteration + "   " + bestSolution.getLocations());

            tabuList.add(currentSolution);
            currentSolution = (MySolution) bestNeighborFound;
            tabuList.updateSize(currentIteration, bestSolution);
        }
        if (stopCondition.mustStop(currentIteration, bestSolution)) {
            return bestSolution;
        }
        return null;

    }

}
