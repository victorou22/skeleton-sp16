import java.util.Observable;
/** 
 *  @author Josh Hug
 */

public class MazeCycles extends MazeExplorer {
    /* Inherits public fields: 
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int found;
    private int[] cameFrom;
    private Maze maze; 
    private boolean setEdges;
    private boolean done;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        found = -1;
        cameFrom = new int[maze.V()];
    }
    
    public void detect(int v){
        marked[v] = true;
        announce();

        for (int w : maze.adj(v)) {
            if (!marked[w]) {
                cameFrom[w] = v;
                detect(w);              
            } else if (cameFrom[v] != w) {
                found = w;
                setEdges = true;
                edgeTo[w] = v;
                announce();
                return;
            }
            if (setEdges) {
                edgeTo[w] = v;
                announce();
                if (v == found) {
                    setEdges = false;
                    done = true;
                }
                return;
            } else if (done) {
                return;
            }
        }
    }

    @Override
    public void solve() {
        detect(maze.xyTo1D(1,1));
    }
}

