package cn.ecnu.tabusearch;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyNeighborSolutionLocator implements BestNeighborSolutionLocator {

    /**
     * Find the non-tabu {@link Solution} with the lowest value.<br>
     * This method doesn't use any Aspiration Criteria.
     */
    @Override
    public Solution findBestNeighbor(List<Solution> neighborsSolutions, final List<Solution> solutionsInTabu) {
        if (neighborsSolutions==null||neighborsSolutions.size()<=0){
            return null;
        }
        //remove any neighbor that is in tabu list
        //禁忌列表中包含neighbor这个solution中的交换就把这个solution删除，因为这个solution的交换操作刚刚使用过
//        CollectionUtils.filterInverse(neighborsSolutions, new Predicate<Solution>() {
//            @Override
//            public boolean evaluate(Solution neighbor) {
//                for (Solution  s: solutionsInTabu){
//                    MySolution s1=(MySolution) s;
//                    MySolution s2=(MySolution) s;
//                    if (s2.swaps.size()>0&&s1.swaps.contains(s2.swaps.get(s2.swaps.size()-1))){
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//        if (neighborsSolutions==null||neighborsSolutions.size()<=0){
//            return null;
//        }
        //sort the neighbors
        Collections.sort(neighborsSolutions, new Comparator<Solution>() {
            @Override
            public int compare(Solution a, Solution b) {
                return a.getValue().compareTo(b.getValue());
            }
        });

        //get the neighbor with lowest value
        return neighborsSolutions.get(0);
    }

    @Override
    public Solution findAmnestyNeighbor(List<Solution> neighborsSolutions, final List<Solution> solutionsInTabu) {
        //sort the neighbors
        Collections.sort(neighborsSolutions, new Comparator<Solution>() {
            @Override
            public int compare(Solution a, Solution b) {
                return a.getValue().compareTo(b.getValue());
            }
        });

        //get the neighbor with lowest value
        return neighborsSolutions.get(0);
    }
}
