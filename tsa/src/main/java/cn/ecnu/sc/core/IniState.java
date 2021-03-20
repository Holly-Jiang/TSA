package cn.ecnu.sc.core;

import cn.ecnu.sc.graph.IniEdge;
import cn.ecnu.sc.graph.IniGraph;
import cn.ecnu.sc.graph.IniNode;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class IniState {

        public Integer[] core_1; // stores for each target graph node to which query graph node it maps ("-1" indicates no mapping)
        public Integer[] core_2; // stores for each query graph node to which target graph node it maps ("-1" indicates no mapping)
        public Map<List<Integer>,Integer> max_core_2;
        public Integer[] inimap;
        public int[] in_1; 	// stores for each target graph node the depth in the search tree at which it entered "T_1 in" or the mapping ("-1" indicates that the node is not part of the set)
        public int[] in_2; 	// stores for each query graph node the depth in the search tree at which it entered "T_2 in" or the mapping ("-1" indicates that the node is not part of the set)
        public int[] out_1; // stores for each target graph node the depth in the search tree at which it entered "T_1 out" or the mapping ("-1" indicates that the node is not part of the set)
        public int[] out_2; // stores for each query graph node the depth in the search tree at which it entered "T_2 out" or the mapping ("-1" indicates that the node is not part of the set)

        public HashSet<Integer> T1in;	// nodes that not yet in the partial mapping, that are the destination of branches start from target graph
        public HashSet<Integer> T1out;	// nodes that not yet in the partial mapping, that are the origin of branches end into target graph
        public HashSet<Integer> T2in;	// nodes that not yet in the partial mapping, that are the destination of branches start from query graph
        public HashSet<Integer> T2out;	// nodes that not yet in the partial mapping, that are the origin of branches end into query graph

        public HashSet<Integer> unmapped1;	// unmapped nodes in target graph
        public HashSet<Integer> unmapped2;	// unmapped nodes in query graph

        public int depth = 0; // current depth of the search tree

        public boolean matched = false;

        public IniGraph targetGraph;
        public IniGraph queryGraph;

        /**
         * Initialize a State
         * @param targetGraph	The big graph
         * @param queryGraph	The small graph
         */
        public IniState(IniGraph targetGraph, IniGraph queryGraph) {

            this.targetGraph = targetGraph;
            this.queryGraph = queryGraph;

            int targetSize = targetGraph.nodes.size();
            int querySize = queryGraph.nodes.size();

            T1in = new HashSet<Integer>(targetSize * 2);
            T1out = new HashSet<Integer>(targetSize * 2);
            T2in = new HashSet<Integer>(querySize * 2);
            T2out = new HashSet<Integer>(querySize * 2);

            unmapped1 = new HashSet<Integer>(targetSize * 2);
            unmapped2 = new HashSet<Integer>(querySize * 2);

            core_1 = new Integer[targetSize];
            core_2 = new Integer[querySize];
            inimap = new Integer[0];
            max_core_2=new HashMap<List<Integer>, Integer>();
            in_1 = new int[targetSize];
            in_2 = new int[querySize];
            out_1 = new int[targetSize];
            out_2 = new int[querySize];

            // initialize values ("-1" means no mapping / not contained in the set)
            // initially, all sets are empty and no nodes are mapped
            for (int i = 0 ; i < targetSize ; i++) {
                core_1[i] = -1;
                in_1[i] = -1;
                out_1[i] = -1;
                unmapped1.add(i);
            }
            for (int i = 0 ; i < querySize ; i++) {
                core_2[i] = -1;
                in_2[i] = -1;
                out_2[i] = -1;
                unmapped2.add(i);
            }
        }

        public Boolean inM1(int nodeId) {
            return (core_1[nodeId] > -1);
        }

        public Boolean inM2(int nodeId) {
            return (core_2[nodeId] > -1);
        }

        public Boolean inT1in(int nodeId) {
            return ((core_1[nodeId] == -1) && (in_1[nodeId] > -1));
        }

        public Boolean inT2in(int nodeId) {
            return ((core_2[nodeId] == -1) && (in_2[nodeId] > -1));
        }

        public Boolean inT1out(int nodeId) {
            return ((core_1[nodeId] == -1) && (out_1[nodeId] > -1));
        }

        public Boolean inT2out(int nodeId) {
            return ((core_2[nodeId] == -1) && (out_2[nodeId] > -1));
        }

        public Boolean inT1(int nodeId) {
            return (this.inT1in(nodeId) || this.inT1out(nodeId));
        }

        public Boolean inT2(int nodeId) {
            return (this.inT2in(nodeId) || this.inT2out(nodeId));
        }

        public Boolean inN1Tilde(int nodeId) {
            return ((core_1[nodeId] == -1) && (in_1[nodeId] == -1) && (out_1[nodeId] == -1));
        }

        public Boolean inN2Tilde(int nodeId) {
            return ((core_2[nodeId] == -1) && (in_2[nodeId] == -1) && (out_2[nodeId] == -1));
        }

        /**
         * Add a new match (targetIndex, queryIndex) to the state
         * @param targetIndex	Index of the node in target graph
         * @param queryIndex	Index of the node in query graph
         */
        public void extendMatch(int targetIndex, int queryIndex) {

            this.core_1[targetIndex] = queryIndex;
            this.core_2[queryIndex] = targetIndex;
            this.unmapped1.remove(targetIndex);
            this.unmapped2.remove(queryIndex);
            this.T1in.remove(targetIndex);
            this.T1out.remove(targetIndex);
            this.T2in.remove(queryIndex);
            this.T2out.remove(queryIndex);

            this.depth++;	// move down one level in the search tree

            IniNode targetNode = targetGraph.nodes.get(targetIndex);
            IniNode queryNode = queryGraph.nodes.get(queryIndex);

            for (IniEdge e : targetNode.inEdges) {
                if (in_1[e.source.id] == -1){	// if the note is not in T1in or mapping
                    in_1[e.source.id] = depth;
                    if (!inM1(e.source.id))		// if not in M1, add into T1in
                        T1in.add(e.source.id);
                }
            }

            for (IniEdge e : targetNode.outEdges) {
                if (out_1[e.target.id] == -1){	// if the note is not in T1out or mapping
                    out_1[e.target.id] = depth;
                    if (!inM1(e.target.id))		// if not in M1, add into T1out
                        T1out.add(e.target.id);
                }
            }

            for (IniEdge e : queryNode.inEdges) {
                if (in_2[e.source.id] == -1){	// if the note is not in T2in or mapping
                    in_2[e.source.id] = depth;
                    if (!inM2(e.source.id))		// if not in M1, add into T2in
                        T2in.add(e.source.id);
                }
            }

            for (IniEdge e : queryNode.outEdges) {
                if (out_2[e.target.id] == -1){	// if the note is not in T2out or mapping
                    out_2[e.target.id] = depth;
                    if (!inM2(e.target.id))		// if not in M1, add into T2out
                        T2out.add(e.target.id);
                }
            }

        }

        /**
         * Remove the match of (targetNodeIndex, queryNodeIndex) for backtrack
         * @param targetNodeIndex
         * @param queryNodeIndex
         */
        public void backtrack(int targetNodeIndex, int queryNodeIndex) {

            core_1[targetNodeIndex] = -1;
            core_2[queryNodeIndex] = -1;
            unmapped1.add(targetNodeIndex);
            unmapped2.add(queryNodeIndex);

            for (int i = 0 ; i < core_1.length ; i++) {
                if (in_1[i] == depth) {
                    in_1[i] = -1;
                    T1in.remove(i);
                }
                if (out_1[i] == depth) {
                    out_1[i] = -1;
                    T1out.remove(i);
                }
            }
            for (int i = 0 ; i < core_2.length ; i++) {
                if (in_2[i] == depth) {
                    in_2[i] = -1;
                    T2in.remove(i);
                }
                if (out_2[i] == depth) {
                    out_2[i] = -1;
                    T2out.remove(i);
                }
            }

            // put targetNodeIndex and queryNodeIndex back into Tin and Tout sets if necessary
            if (inT1in(targetNodeIndex))
                T1in.add(targetNodeIndex);
            if (inT1out(targetNodeIndex))
                T1out.add(targetNodeIndex);
            if (inT2in(queryNodeIndex))
                T2in.add(queryNodeIndex);
            if (inT2out(queryNodeIndex))
                T2out.add(queryNodeIndex);

            depth--;
        }

        /**
         * Print the current mapping
         */
//	public void printMapping() {
//		for (int i = 0 ; i < core_2.length ; i++) {
//			System.out.print("(" + core_2[i] + "-" + i + ") ");
//		}
//		System.out.println();
//	}

        /**
         * Print the current mapping
         */
        public void printMapping(List<List<Integer> > list) {

            for (int i = 0 ; i < list.size() ; i++) {
                System.out.println("t "+i);
                for (int j=0;j<list.get(0).size();j++){
                    System.out.println(list.get(i).get(j) + " " + j );
                }
            }
        }
        /**
         * Print the current mapping
         */
        public void printMaxMapping() {
            for(Map.Entry<List<Integer> ,Integer>set :max_core_2.entrySet()){
                System.out.println("cout:"+set.getValue());
                for (int j=0;j<set.getKey().size();j++){
                    System.out.println(set.getKey().get(j) + " " + j);
                }
                System.out.println("------");
            }


        }

        /**
         * Write state to file
         */
        public void writeMapping(PrintWriter writer){
            for (int i = 0 ; i < core_2.length ; i++) {
                writer.write("(" + core_2[i] + "-" + i + ") ");
            }
            writer.write("\n");
        }
        /**
         * Write state to file
         */
        public void writeIniMapping(PrintWriter writer,List<List<Integer> > list){
            for (int i = 0 ; i < list.size() ; i++) {
                writer.write("t "+i+"\n");
                for (int j=0;j<list.get(0).size();j++){
                    writer.write(list.get(i).get(j) + " " + j );
                    writer.write("\n");
                }
            }
            writer.write("t # -1 \n");
            writer.flush();
        }
    }