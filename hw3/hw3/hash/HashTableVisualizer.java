package hw3.hash;

import java.util.HashSet;
import java.util.Set;

public class HashTableVisualizer {

    public static void main(String[] args) {
        /* scale: StdDraw scale
           N:     number of items
           M:     number of buckets */

        double scale = 1.0;
        int N = 50;
        int M = 20;

        HashTableDrawingUtility.setScale(scale);
        Set<Oomage> oomies = new HashSet<Oomage>();
        for (int i = 0; i < N; i += 1) {
            oomies.add(ComplexOomage.randomComplexOomage());
        }
        visualize(oomies, M, scale);
    }

    public static void visualize(Set<Oomage> set, int M, double scale) {
        HashTableDrawingUtility.drawLabels(M);
        int[] buckets = new int[M];

        for (Oomage oo : set) {
          int bucketPos = (oo.hashCode() & 0x7FFFFFFF) % M;
          buckets[bucketPos]++;
          oo.draw(HashTableDrawingUtility.xCoord(buckets[bucketPos]), HashTableDrawingUtility.yCoord(bucketPos, M), scale);
        }

        /* When done with visualizer, be sure to try 
           scale = 0.5, N = 2000, M = 100. */           
    }
} 
