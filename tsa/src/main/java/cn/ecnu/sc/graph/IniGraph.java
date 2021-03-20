package cn.ecnu.sc.graph;

import java.util.ArrayList;

public class IniGraph {
        public String name; // name of the graph
        public ArrayList<IniNode> nodes = new ArrayList<IniNode>(); // list of all nodes
        public ArrayList<IniEdge> edges = new ArrayList<IniEdge>(); // list of all edges

        private int[][] adjacencyMatrix; // stores graph structure as adjacency matrix (-1: not adjacent, >=0: the edge label)
        private boolean adjacencyMatrixUpdateNeeded = true; // indicates if the adjacency matrix needs an update

        public IniGraph(String name) {
            this.name = name;
        }

        public void addNode(int id, int label) {
            nodes.add(new IniNode(this, id, label));
            this.adjacencyMatrixUpdateNeeded = true;
        }

        public void addEdge(IniNode source, IniNode target, int label) {
            edges.add(new IniEdge(this, source, target, label));
            this.adjacencyMatrixUpdateNeeded = true;
        }

        public void addEdge(int sourceId, int targetId, int label) {
            this.addEdge(this.nodes.get(sourceId), this.nodes.get(targetId), label);
        }


        /**
         * Get the adjacency matrix
         * Reconstruct it if it needs an update
         * @return Adjacency Matrix
         */
        public int[][] getAdjacencyMatrix() {

            if (this.adjacencyMatrixUpdateNeeded) {

                int k = this.nodes.size();
                this.adjacencyMatrix = new int[k][k];	// node size may have changed
                for (int i = 0 ; i < k ; i++)			// initialize entries to -1
                    for (int j = 0 ; j < k ; j++)
                        this.adjacencyMatrix[i][j] = -1;

                for (IniEdge e : this.edges) {
                    this.adjacencyMatrix[e.source.id][e.target.id] = e.label; // label must bigger than -1
                }
                this.adjacencyMatrixUpdateNeeded = false;
            }
            return this.adjacencyMatrix;
        }

        // prints adjacency matrix to console
        public void printGraph() {
            int[][] a = this.getAdjacencyMatrix();
            int k = a.length;

            System.out.print(this.name + " - Nodes: ");
            for (IniNode n : nodes) System.out.print(n.id + " ");
            System.out.println();
            for (int i = 0 ; i < k ; i++) {
                for (int j = 0 ; j < k ; j++) {
                    System.out.print(a[i][j] + " ");
                }
                System.out.println();
            }
        }
    }

