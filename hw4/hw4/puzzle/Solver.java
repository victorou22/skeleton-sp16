package hw4.puzzle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;
import java.util.ArrayList;
import java.util.Collections;

public class Solver {
    private int moves = 0;
    private MinPQ<BoardNode> pq;
    private ArrayList<Board> solutionList;

    /** Solver program for the 8 Puzzle using the A* graph search algorithm
    /*  Solves the puzzle that is input into the constructor. (Assumes that the input board is solvable)
    /*  Stores each board state as a BoardNode in a Min Priority Queue, sorted by Manhattan Distance
    /*  Implements the A* search algorithm, using the number of moves taken plus the Manhattan distance as heuristic */
     public Solver(Board initialBoard) {
        pq = new MinPQ<BoardNode>();
        BoardNode initialNode = new BoardNode(initialBoard, 0, null);
        pq.insert(initialNode);

        while(!pq.isEmpty()) {
            /** While the minPQ is not empty, check each possible move and choose the best one according to A* algorithm */
            BoardNode currNode = pq.delMin();
            if (currNode.getBoard().isGoal()) {
                /** If solved board is found, move back through the nodes,
                 *  Storing the Boards in an ArrayList, starting from the end
                 *  So when the ArrayList is returned, the solution is in order
                 */
                moves = currNode.getMoves();
                solutionList = new ArrayList<Board>(Collections.nCopies(currNode.getMoves()+1, null));
                for (int i = currNode.getMoves(); i >= 0; i--) {
                    solutionList.set(i, currNode.getBoard());
                    currNode = currNode.previousNode();
                }
                break;
            }
            /** Put all possible moves into the minPQ, making sure to exclude the Board state that the current Node came from */
            Iterable<Board> neighbors = BoardUtils.neighbors(currNode.getBoard());
            for (Board b : neighbors) {
                BoardNode previousNode = currNode.previousNode();
                if (previousNode == null || !b.equals(previousNode.getBoard())) {
                    pq.insert(new BoardNode(b, currNode.getMoves() + 1, currNode));
                }
            }
        }
    }

    /** Returns the minimum number of moves to reach solution */
    public int moves() {
        return moves;
    }

    /** Returns an iterable holding all the board states to reach the solution */
    public Iterable<Board> solution() {
        return solutionList;
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board initial = new Board(tiles);
        Solver solver = new Solver(initial);
        StdOut.println("Minimum number of moves = " + solver.moves());
        for (Board board : solver.solution()) {
            StdOut.println(board);
       }
    }

}
