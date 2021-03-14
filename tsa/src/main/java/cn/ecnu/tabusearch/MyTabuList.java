package cn.ecnu.tabusearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.ecnu.tabusearch.TabuList;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class MyTabuList implements TabuList {

    private CircularFifoQueue<Solution> tabuList;

    public MyTabuList(Integer size) {
        this.tabuList = new CircularFifoQueue<Solution>(size);
    }

    @Override
    public void add(Solution solution) {
        tabuList.add(solution);
    }

    @Override
    public Boolean contains(Solution solution) {
        return tabuList.contains(solution);
    }

    @Override
    public void updateSize(Integer currentIteration, Solution bestSolutionFound) {

    }

    @Override
    public Iterator<Solution> iterator() {
        return tabuList.iterator();
    }

    /**
     * This method does not perform any update in the tabu list,
     * due to the fixed size nature of this implementation
     */

}

