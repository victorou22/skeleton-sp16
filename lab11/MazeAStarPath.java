import java.util.Observable;
import java.util.PriorityQueue;
import java.util.Comparator;
/**
 *  @author Josh Hug
 */

public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }
    
    /*
    @Override
    public class tileComp implements Comparator<Node> {
        
        public int compare(Object o1, Object o2) {
            Node node1 = (Node) o1;
            Node node2 = (Node) o2;
            
            return node1.getPriority() - node2.getPriority();
        }
    }*/
    
    public class Node implements Comparable<Node> {
        private int value;
        private int priority;
        
        public Node(int val, int p) {
            value = val;
            priority = p;
        }
        
        public int getValue() {
            return value;
        }
        
        public int getPriority() {
            return priority;
        }
        
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.priority, other.getPriority());
        }
    }
    
    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        return Math.abs(maze.toX(v) - maze.toX(t)) + Math.abs(maze.toY(v) - maze.toX(t));
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked() {
        return -1;
        /* You do not have to use this method. */
    }

    /** Performs an astar search from vertex s. */
    private void astar(int s) {
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        pq.add(new Node(s, h(s)));
        marked[s] = true;
        
        while (!pq.isEmpty()) {
            Node currNode = pq.poll();
            announce();
            if (currNode.getValue() == t) {
                return;
            }
            for (int v : maze.adj(currNode.getValue())) {
                if (!marked[v]) {
                    pq.add(new Node(v, h(v)));
                    edgeTo[v] = currNode.getValue();
                    marked[v] = true;
                    distTo[v] = distTo[currNode.getValue()] + 1;
                }
                
            }
            
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

}

