package cn.ecnu.vf2.graph;

public class IniEdge {

        public IniGraph graph; 	// the graph to which the edge belongs

        public IniNode source; 	// the source / origin of the edge
        public IniNode target; 	// the target / destination of the edge
        public int label; 	// the label of this edge

        // creates new edge
        public IniEdge(IniGraph g, IniNode source, IniNode target, int label) {
            this.graph = g;
            this.source = source; // store source
            source.outEdges.add(this); // update edge list at source
            this.target = target; // store target
            target.inEdges.add(this); // update edge list at target
            this.label = label;
        }

    }

