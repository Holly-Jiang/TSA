package cn.ecnu.vf2.graph;

import java.util.ArrayList;

public class IniNode {

        public IniGraph graph; // the graph to which the node belongs

        public int id; // a unique id - running number
        public int label; // for semantic feasibility checks

        public ArrayList<IniEdge> outEdges = new ArrayList<IniEdge>(); // edges of which this node is the origin
        public ArrayList<IniEdge> inEdges = new ArrayList<IniEdge>(); // edges of which this node is the destination

        public IniNode(IniGraph g, int id, int label) {
            this.graph = g;
            this.id = id;
            this.label = label;
        }
    }
