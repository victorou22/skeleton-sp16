package hw4.puzzle;

import java.util.Arrays;

public class Board {
    private final int[][] tiles;
    private int hammingDist;
    private int manhattanDist;

    public Board(int[][] tiles) {
        this.tiles = new int[tiles.length][tiles[0].length];
        hammingDist = 0;
        manhattanDist = 0;

        //Copy the input board and compute hammingDist and manhattanDist
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                this.tiles[i][j] = tiles[i][j];         //Create immutable copy of the input tiles

                if (tiles[i][j] == 0 || tiles[i][j] == xyToIndex(i, j)) {       //If tile in correct position, continue
                    continue;
                }
                hammingDist++;
                int manhattanDifference = Math.abs(tiles[i][j] - xyToIndex(i, j));
                manhattanDist += manhattanDifference/tiles[0].length + manhattanDifference % tiles[0].length;
            }
        }
    }

    public int tileAt(int i, int j) {
        return tiles[i][j];
    }

    public int size() {
        return tiles.length;
    }

    private int xyToIndex(int x, int y) {
        return x*tiles[0].length + y + 1;
    }

    public int hamming() {
        return hammingDist;
    }

    public int manhattan() {
        return manhattanDist;
    }

    public boolean isGoal() {
        return manhattanDist == 0;
    }

    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }
        if (this == null || y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board b = (Board) y;
        return Arrays.deepEquals(this.tiles, b.tiles);
    }

    // Returns the string representation of the board.
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i,j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }
    /**
    public static void main(String[] args) {
        int[][] tiles = {{8,1,3}, {4,0,2}, {7,6,5}};
        int[][] tiles2 = {{8,1,3}, {4,0,2}, {7,6,5}};
        Board b = new Board(tiles);
        Board b2 = new Board(tiles2);
        System.out.println(b);
        System.out.println("The Hamming Distance is " + String.valueOf(b.hamming()));
        System.out.println("The Manhattan Distance is " + String.valueOf(b.manhattan()));
        System.out.println("Solved: " + b.isGoal());
        System.out.println("Both boards equal: " + b.equals(b2));
    }*/

}
