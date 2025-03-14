package hw4.puzzle;

public class BoardNode implements Comparable<BoardNode> {
    private Board b;
    private int moves;
    private int priority;
    private BoardNode previousNode;

    public BoardNode(Board initialBoard, int moves, BoardNode previous) {
        b = initialBoard;
        this.moves = moves;
        priority = b.manhattan() + this.moves;
        previousNode = previous;
    }

    public BoardNode previousNode() {
        return this.previousNode;
    }

    public int getMoves(){
        return moves;
    }

    public Board getBoard() {
        return b;
    }

    public int compareTo(BoardNode other) {
        return Integer.compare(this.priority, other.priority);
    }
}
