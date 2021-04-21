/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edmonds.karp;


/*





  *************************
     	Hawazin salem Aljehani  1805587
	Jana Aloraifi           1805259
	Rehab Nuhayd            1808142
	
    ***************************
References :

 http://www.cs.ucf.edu/~dmarino/ucf/cop3503/sampleprogs/networkflow.java
 https://tutorialspoint.dev/data-structure/graph-data-structure/minimum-cut-in-a-directed-graph


** We joined the codes and added some comments


*/
import java.util.*;

 class Edgee { // Edge class to create many edge 

	private int capacity; // integer number represent the capacity
	private int flow; // integer number represent the flow

	public Edgee(int cap) { // Edge contractor to add new Edge
		capacity = cap;
		flow = 0;
	}

	public int maxPushForward() { // updated the capacity 
		return capacity - flow;
	}

	public int maxPushBackward() { // updated the capacity 
		return flow;
	}

	public boolean pushForward(int moreflow) { // check if we can do pushForward

		// We can't push through this much flow.
		if (flow+moreflow > capacity)
			return false;

		// Push through.
		flow += moreflow;
		return true;
	}

	public boolean pushBack(int lessflow) { // check if we can do pushBack

		// Not enough to push back on.
		if (flow < lessflow)
			return false;
            
                // push back on.
		flow -= lessflow;
		return true;
	}
}

class direction { // direction class

	public int prev; // integer number represent the prev
	public boolean forward;  // integer number represent the forward 

	public direction(int node, boolean dir) { // direction class to create direction for node 
		prev = node;
		forward = dir;
	}

	public String toString() { // print the prev with his direction 
		if (forward)
			return "" + (prev+1) + "->"; // because our graph start from node 1 , not zero 
		else
			return "" + (prev+1) + "<-";// because our graph start from node 1 , not zero 
	}
}

public class Edmonds_karp {  // Edmonds_karp class
	
	private final static boolean DEBUG = false;
	private final static boolean PRINTPATH = true;

	private Edgee[][] adjMat; // adjMat array from class Edge
	private int source; // integer number represent the source
	private int dest;  //integer number represent the dest

        
	// All positive entries in flows should represent valid flows
	// between vertices. All other entries must be 0 or negative.
	public Edmonds_karp(int[][] flows, int start, int end) {

		source = start; // initial the source
		dest = end; //  initial the dest
                
		adjMat = new Edgee[flows.length][flows.length]; //  initial array for the network

		for (int i=0; i<flows.length; i++) {
			for (int j=0; j<flows[i].length; j++) {

				// Fill in this flow.
				if (flows[i][j] > 0)
					adjMat[i][j] = new Edgee(flows[i][j]);
				else
					adjMat[i][j] = null;
			}
		}
	}

	public ArrayList<direction> findAugmentingPath() { // function findAugmentingPath

		// This will store the previous node visited in the BFS.
		direction[] prev = new direction[adjMat.length];
		boolean[] inQueue = new boolean[adjMat.length];
		for (int i=0; i<inQueue.length; i++)
			inQueue[i] = false;

		// The source has no previous node.
		prev[source] = new direction(-1, true);

		LinkedList<Integer> bfs_queue = new LinkedList<Integer>();
		bfs_queue.offer(new Integer(source));
		inQueue[source] = true;

		// Our BFS will go until we clear out the queue.
		while (bfs_queue.size() > 0) {

			// Add all the new neighbors of the current node.
			Integer next = bfs_queue.poll();
			if (DEBUG) System.out.println("Searching " + next);

			// Find all neighbors and add into the queue. These are forward edges.
			for (int i=0; i<adjMat.length; i++)
				if (!inQueue[i] && adjMat[next][i] != null && adjMat[next][i].maxPushForward() > 0) {
					bfs_queue.offer(new Integer(i));
					inQueue[i] = true;
					prev[i] = new direction(next, true);
				}

			// Now look for back edges.
			for (int i=0; i<adjMat.length; i++)
				if (!inQueue[i] && adjMat[i][next] != null && adjMat[i][next].maxPushBackward() > 0) {
					bfs_queue.offer(new Integer(i));
					inQueue[i] = true;
					prev[i] = new direction(next, false);
				}
		}

		// No augmenting path found.
		if (!inQueue[dest])
			return null;

		ArrayList<direction> path = new ArrayList<direction>();

		direction place = prev[dest];

		direction dummy = new direction(dest, true);
		path.add(dummy);

		// Build the path backwards.
		while (place.prev != -1) {
			path.add(place);
			place = prev[place.prev];
		}

		// Reverse it now.
		Collections.reverse(path);

		return path;
	}

	// Run the Max Flow Algorithm here.
	public int getMaxFlow() { // class getMaxFlow 

		int flow = 0; // inital flow

		ArrayList<direction> nextpath = findAugmentingPath();
		
                //  print the first path 
		if (DEBUG || PRINTPATH) {
			System.out.println("Found one augmenting path.");
			for (int i=0; i<nextpath.size(); i++){
                            if (i==nextpath.size()-1){ // if it the last nod , Make it just number
                                // String a = nextpath.get(i);
                                System.out.print((nextpath.get(i)).toString().substring(0, 1));
                                
                            }else {
                             System.out.print(nextpath.get(i)+" ");    
                            } 
                        }
			
			System.out.println();
		}
		
		// Loop until there are no more augmenting paths.
		while (nextpath != null) {

			// Check what the best flow through this path is.
			int this_flow = Integer.MAX_VALUE;
			for (int i=0; i<nextpath.size()-1; i++) {

				if (nextpath.get(i).forward) {
					this_flow = Math.min(this_flow, adjMat[nextpath.get(i).prev][nextpath.get(i+1).prev].maxPushForward());
				}
				else {
					this_flow = Math.min(this_flow, adjMat[nextpath.get(i+1).prev][nextpath.get(i).prev].maxPushBackward());
				}
			}

			// Now, put this flow through.
			for (int i=0; i<nextpath.size()-1; i++) {

				if (nextpath.get(i).forward) {
					adjMat[nextpath.get(i).prev][nextpath.get(i+1).prev].pushForward(this_flow);
				}
				else {
					adjMat[nextpath.get(i+1).prev][nextpath.get(i).prev].pushBack(this_flow);
				}
			}

			// Add this flow in and then get the next path.
			if (DEBUG || PRINTPATH) System.out.println("Adding "+this_flow);
			flow += this_flow;
                        System.out.println ("Updated flow : "+flow);
			nextpath = findAugmentingPath();
                        
                        // cheak if ther is another path !
			if (nextpath != null && (DEBUG || PRINTPATH)) {

				System.out.println("Found another augmenting path.");
				for (int i=0; i<nextpath.size(); i++){
                            if (i==nextpath.size()-1){ // if it the last nod , Make it just number
                                // String a = nextpath.get(i);
                                System.out.print((nextpath.get(i)).toString().substring(0, 1));
                                
                            }else {
                             System.out.print(nextpath.get(i)+" ");    
                            } 
                        }
				System.out.println();
			}

		}

		return flow;
	}
        
    // Returns true if there is a path
    // from source 's' to sink 't' in residual 
    // graph. Also fills parent[] to store the path 
    private static boolean bfs(int[][] rGraph, int s,
                                int t, int[] parent) {
          
        // Create a visited array and mark 
        // all vertices as not visited     
        boolean[] visited = new boolean[rGraph.length];
          
        // Create a queue, enqueue source vertex
        // and mark source vertex as visited     
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(s);
        visited[s] = true;
        parent[s] = -1;
          
        // Standard BFS Loop     
        while (!q.isEmpty()) {
            int v = q.poll();
            for (int i = 0; i < rGraph.length; i++) {
                if (rGraph[v][i] > 0 && !visited[i]) {
                    q.offer(i);
                    visited[i] = true;
                    parent[i] = v;
                }
            }
        }
          
        // If we reached sink in BFS starting 
        // from source, then return true, else false     
        return (visited[t] == true);
    }    

        
          
    // A DFS based function to find all reachable 
    // vertices from s. The function marks visited[i] 
    // as true if i is reachable from s. The initial 
    // values in visited[] must be false. We can also 
    // use BFS to find reachable vertices
    public static void dfs(int[][] rGraph, int s,
                                boolean[] visited) {
        visited[s] = true;
        for (int i = 0; i < rGraph.length; i++) {
                if (rGraph[s][i] > 0 && !visited[i]) {
                    dfs(rGraph, i, visited);
                }
        }
    }    
        
        
            // Prints the minimum s-t cut
    public static void minCut(int[][] graph, int s, int t) {
        int u,v;
          
        // Create a residual graph and fill the residual 
        // graph with given capacities in the original 
        // graph as residual capacities in residual graph
        // rGraph[i][j] indicates residual capacity of edge i-j
        int[][] rGraph = new int[graph.length][graph.length]; 
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                rGraph[i][j] = graph[i][j];
            }
        }
  
        // This array is filled by BFS and to store path
        int[] parent = new int[graph.length]; 
          
        // Augment the flow while tere is path from source to sink     
        while (bfs(rGraph, s, t, parent)) {
              
            // Find minimum residual capacity of the edhes 
            // along the path filled by BFS. Or we can say 
            // find the maximum flow through the path found.
            int pathFlow = Integer.MAX_VALUE;         
            for (v = t; v != s; v = parent[v]) {
                u = parent[v];
                pathFlow = Math.min(pathFlow, rGraph[u][v]);
            }
              
            // update residual capacities of the edges and 
            // reverse edges along the path
            for (v = t; v != s; v = parent[v]) {
                u = parent[v];
                rGraph[u][v] = rGraph[u][v] - pathFlow;
                rGraph[v][u] = rGraph[v][u] + pathFlow;
            }
        }
          
        // Flow is maximum now, find vertices reachable from s     
        boolean[] isVisited = new boolean[graph.length];     
        dfs(rGraph, s, isVisited);
          
        // Print all edges that are from a reachable vertex to
        // non-reachable vertex in the original graph     
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                if (graph[i][j] > 0 && isVisited[i] && !isVisited[j]) {
                    System.out.print("("+(i+1 )+ " - " + (j+1)+")"); //because our graph start from node 1 , not zero 
                }
            }
        }
    }


	
	public static void main(String[] args) {
            /*
            
                   
            
            
            */
            
            
            
                // inital the graph 
		int[][] graph = new int[7][7];
		graph[0][1] = 2;
		graph[0][2] = 7;
                
		graph[1][4] = 4;
                graph[1][3] = 3;
                
		graph[2][3] = 4;
		graph[2][4] = 2;
		
		graph[3][5] = 1;

                graph[4][5] = 5;



		Edmonds_karp mine = new Edmonds_karp(graph, 0, 5); // 	The maximum flow of the network

		int answer = mine.getMaxFlow(); // get the answer from mine graph 
                
		System.out.println("The maximum flow of the network "+answer);
                System.out.println("\n----------------------------------------");
                
                System.out.print("The min-cut is C(X,Xc)= {");
                minCut(graph, 0, 5);
                System.out.println("}");

	}

}
