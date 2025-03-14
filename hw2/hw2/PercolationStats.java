package hw2;          

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import java.lang.IllegalArgumentException;             

public class PercolationStats {
    private int[] numSitesToPercolate;
    private double mMean;
    private double mStdDev;
    private double mConfidenceLow;
    private double mConfidenceHigh;
    
    public PercolationStats(int N, int T) {   // perform T independent experiments on an N-by-N grid
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("N and T must be greater than zero.");
        }
        numSitesToPercolate = new int[T];
        
        for (int i = 0; i < T; i++) {
            Percolation grid = new Percolation(N);
            while (!grid.percolates()) {
                grid.open(StdRandom.uniform(N), StdRandom.uniform(N));
            }
            numSitesToPercolate[i] = grid.numberOfOpenSites();
        }
        
        mMean = StdStats.mean(numSitesToPercolate);
        mStdDev = StdStats.stddev(numSitesToPercolate);
        mConfidenceLow = mMean - 1.96*mStdDev/Math.sqrt(T);
        mConfidenceHigh = mMean + 1.96*mStdDev/Math.sqrt(T);
        
    }
    
    public double mean() {                    // sample mean of percolation threshold
        return mMean;
    }
    
    public double stddev() {                  // sample standard deviation of percolation threshold
        return mStdDev;
    }
    
    public double confidenceLow() {           // low endpoint of 95% confidence interval
        return mConfidenceLow;
    }
    
    public double confidenceHigh() {          // high endpoint of 95% confidence interval
        return mConfidenceHigh;
    }
    
    public static void main(String[] args) {
        int N = Integer.valueOf(args[0]);
        int T = Integer.valueOf(args[1]);
        PercolationStats stats = new PercolationStats(N, T);
        System.out.println("Mean: " + stats.mean());
        System.out.println("Percolation Threshold: " + String.valueOf(stats.mean()/(N*N)));
        System.out.println("StdDev: " + stats.stddev());
        System.out.println("Confidence Low: " + stats.confidenceLow());
        System.out.println("Confidence High: " + stats.confidenceHigh());
        
    }
}                       
