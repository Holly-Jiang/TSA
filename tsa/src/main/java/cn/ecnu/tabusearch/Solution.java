package cn.ecnu.tabusearch;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Common interface for tabu search solutions.<br>
 * Subclasses must use the objective function to calculate the value of the objects of this interface,
 * while the neighbors generations also must be performed here.<br>
 * The {@link #equals()} and {@link #hashCode()} methods must be overridden to ensure the algorithm
 * correctness.
 * 
 * @author Alex Ferreira
 *
 */
public interface Solution {
	
	/**
	 * Get the value of this solution.<br>
	 * Is the same value returned by the {@link Solution} objective function
	 * @return the value of this solution
	 */
	Double getValue();
	/**
	 * Get the neighbors of this solution
	 * @return the neighbors of this solution
	 */
	List<Solution> getNeighbors(int type) ;

//	//这个交换的得分
//	Double value=0.0;
//	//可能是下一个待交换列表
//	List<Solution> neighbors=new ArrayList<>();
//	//当前层需要满足相邻关系的门
//	List<List<Integer>> currentLayers=new ArrayList<>();
//	//距离矩阵
//	ShortPath [][]  dist=new ShortPath[20][20];
//	//还需要一个物理结构的距离矩阵和当前映射
//	//index表示逻辑qubit locations[index]表示逻辑qubit映射的物理qubit位置,-1表示没有进行映射
//	List<Integer> locations=new ArrayList<>();
//	//index表示物理qubit，qubits[index]表示物理qubit映射的哪个逻辑qubit ，-1 表示没有进行映射
//	List<Integer> qubits=new ArrayList<>();


	
}
