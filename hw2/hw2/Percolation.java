package hw2;                       

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;

public class Percolation {
    private boolean[][] grid;
    private int dimension;
    private WeightedQuickUnionUF unionSet;
    private int count;
    private int TOP;        //index of a vitual node connecting the top row of the grid
    private int BOTTOM;     //index of a virtual node connecting the bottom row of the grid
    
    
    private int xyTo1D(int x, int y) {          //returns a translation of xy coordinates into 1D coordinates so that each grid has a unique id
        return x*dimension + y;
    }
    
    public Percolation(int N) {                // create N-by-N grid, with all sites initially blocked
        if (N <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive.");
        }
        grid = new boolean[N][N];
        unionSet = new WeightedQuickUnionUF(N*N+2);     //UnionFind data structure used to store connected components. Additional 2 indices for the top and bottom vitual nodes to improve the efficiency of checking for fullness and percolation
        dimension = N;
        TOP = N*N;
        BOTTOM = N*N + 1;
    }
    
    private void unionWithNeighbors(int row, int col) {    //checks neighbors and unions with them if site is open
        if (row == 0) {                                                 //if top row, union with top virtual site
            unionSet.union(this.xyTo1D(row, col), TOP);
        }
        if (row == dimension - 1) {                                     //if bottom row, union with bottom virtual site
            unionSet.union(this.xyTo1D(row, col), BOTTOM);
        }
        if (row > 0 && this.isOpen(row - 1, col)) {                    //union above site
            unionSet.union(this.xyTo1D(row, col), this.xyTo1D(row - 1, col));
        }
        if (col < (dimension - 1) && this.isOpen(row, col + 1)) {     //union right of site
            unionSet.union(this.xyTo1D(row, col), this.xyTo1D(row, col + 1));
        }
        if (row < (dimension - 1) && this.isOpen(row + 1, col)) {     //union below site
            unionSet.union(this.xyTo1D(row, col), this.xyTo1D(row + 1, col));
        }
        if (col > 0 && this.isOpen(row, col - 1)) {             //union left of site
            unionSet.union(this.xyTo1D(row, col), this.xyTo1D(row, col - 1));
        }
    }
    
    public void open(int row, int col) {       // open the site (row, col) if it is not open already
        if (row >= dimension || col >= dimension || row < 0 || col < 0) {
            throw new IndexOutOfBoundsException("Row and column must be between 0 and " + String.valueOf(dimension-1));
        }
        if (grid[row][col]) {
            return;
        }
        grid[row][col] = true;
        count++;
        this.unionWithNeighbors(row, col);
    }
    
    public boolean isOpen(int row, int col) {  // is the site (row, col) open?
        if (row >= dimension || col >= dimension || row < 0 || col < 0) {
            throw new IndexOutOfBoundsException("Row and column must be between 0 and " + String.valueOf(dimension-1));
        }
        return grid[row][col];
    }
    
    public boolean isFull(int row, int col) {  // is the site (row, col) full?
        if (row >= dimension || col >= dimension || row < 0 || col < 0) {
            throw new IndexOutOfBoundsException("Row and column must be between 0 and " + String.valueOf(dimension-1));
        }
        return unionSet.connected(this.xyTo1D(row, col), TOP);
    }
    
    public int numberOfOpenSites() {           // number of open sites
        return count;
    }
    
    public boolean percolates() {              // does the system percolate?
        return unionSet.connected(TOP, BOTTOM);
    }
    
    public static void main(String[] args) {   // unit testing (not required)
        Percolation per = new Percolation(2);
        per.open(0, 0);
        per.open(1, 1);
        per.open(1, 0);
        System.out.println("(0,0) is " + per.isOpen(0,0));
        System.out.println("(1,1) is " + per.isOpen(1,1));
        System.out.println("(1,0) is " + per.isOpen(1,0));
        System.out.println("(0,0) is " + per.isFull(0,0));
        System.out.println("(1,1) is " + per.isFull(1,1));
        System.out.println("(1,0) is " + per.isFull(1,0));
        System.out.println("(1,1) is " + per.isFull(1,1));
        System.out.println(per.numberOfOpenSites());
        System.out.println(per.percolates());
        
    }
}                     
