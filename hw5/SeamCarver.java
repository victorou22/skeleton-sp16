import edu.princeton.cs.algs4.Picture;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private static Picture picture;
    private static double[][] energies;
    
    public SeamCarver(Picture picture) {
        this.picture = picture;
        energies = calcEnergies(this.picture);
    }
    
    private ArrayList<Color> getPixels(int col, int row, Picture picture) {      //Returns the four surrounding colors in order of above, below, left, right
        ArrayList<Color> pixels = new ArrayList<>();
        if (row == 0) {
            pixels.add(picture.get(col, picture.height() - 1));
        } else {
            pixels.add(picture.get(col, row - 1));
        }
        if (row == picture.height() - 1) {
            pixels.add(picture.get(col, 0));
        } else {
            pixels.add(picture.get(col, row + 1));
        }
        if (col == 0) {
            pixels.add(picture.get(picture.width() - 1, row));
        } else {
            pixels.add(picture.get(col - 1, row));
        }
        if (col == picture.width() - 1) {
            pixels.add(picture.get(0, row));
        } else {
            pixels.add(picture.get(col + 1, row));
        }
        return pixels;
    }
        
    private double[][] calcEnergies(Picture picture) {
        double[][] energies = new double[this.height()][this.width()];
        
        for (int i = 0; i < this.height(); i++) {
            for (int j = 0; j < this.width(); j++) {
                ArrayList<Color> pixels = getPixels(j, i, picture);
                Color up = pixels.get(0);
                Color down = pixels.get(1);
                Color left = pixels.get(2);
                Color right = pixels.get(3);
                
                int delRedX = right.getRed() - left.getRed();
                int delGreenX = right.getGreen() - left.getGreen();
                int delBlueX = right.getBlue() - left.getBlue();
                int gradientX = delRedX*delRedX + delGreenX*delGreenX + delBlueX*delBlueX;
                
                int delRedY = down.getRed() - up.getRed();
                int delGreenY = down.getGreen() - up.getGreen();
                int delBlueY = down.getBlue() - up.getBlue();
                int gradientY = delRedY*delRedY + delGreenY*delGreenY + delBlueY*delBlueY;
                
                int energy = gradientX + gradientY;
                energies[i][j] = energy;
            }
        }
        return energies;
    }
    
    public Picture picture() {                       // current picture
        return this.picture;
    }
    
    public int width() {                         // width of current picture
        return picture.width();
    }
    
    public int height() {                        // height of current picture
        return picture.height();
    }
    
    public double energy(int x, int y) {            // energy of pixel at column x and row y
        return energies[y][x];
    }
    
    public int[] findHorizontalSeam()  {            // sequence of indices for horizontal seam
        return findVerticalSeam();
    }

    private double findMinCostPath(int col, int row, double[][] costs) {      //returns the minCostPath for that row of the seam
        if (row == 0) {
            return energies[row][col];
        } else {
            double left = Double.MAX_VALUE;
            double right = Double.MAX_VALUE;
            double middle = costs[row - 1][col];

            if (col >= 0 && col < this.width() - 1) {
                right = costs[row - 1][col + 1];
            }
            if (col > 0 && col <= this.width() - 1) {
                left = costs[row - 1][col - 1];
            }

            double min = Math.min(Math.min(left, middle), right);
            return energies[row][col] + min;
        }
    }

    private int findMinSeamCol(int col, int row, double[][] costs) {
        double target = costs[row][col] - energies[row][col];

        if (col > 0) {
            if (costs[row - 1][col - 1] == target) {
                return col - 1;
            }
        }
        if (col < this.width() - 1) {
            if (costs[row - 1][col + 1] == target) {
                return col + 1;
            }
        }
        if (costs[row - 1][col] == target) {
            return col;
        } else {
            System.out.print("Seam recreation error!");
            return -1;
        }

    }
    
    public int[] findVerticalSeam() {              // sequence of indices for vertical seam
        int[] seam = new int[this.height()];
        double[][] costs = new double[this.height()][this.width()];

        /* Builds the cost matrix using dynamic programming */
        for (int i = 0; i < this.height(); i++) {
            for (int j = 0; j < this.width(); j++) {
                costs[i][j] = findMinCostPath(j, i, costs);
            }
        }

        /* Find the column of the min seam at the bottom of the matrix */
        int seamCol = 0;
        for (int j = 1; j < this.width(); j++) {
            if (costs[this.height() - 1][j] < costs[this.height() - 1][seamCol]) {
                seamCol = j;
            }
        }

        /* Recreate the seam */
        for (int i = this.height() - 1; i > 0; i--) {
            seam[i] = seamCol;
            seamCol = findMinSeamCol(seamCol, i, costs);
        }

        seam[0] = seamCol;
        return seam;
    }
    
    public void removeHorizontalSeam(int[] seam) {   // remove horizontal seam from picture
        this.picture = SeamRemover.removeHorizontalSeam(this.picture, seam);
        energies = calcEnergies(this.picture);
    }
    
    public void removeVerticalSeam(int[] seam) {      // remove vertical seam from picture

        this.picture = SeamRemover.removeVerticalSeam(this.picture, seam);
        energies = calcEnergies(this.picture);
    }
    
    public static void main(String[] args) {
        Picture p = new Picture("images/6x5.png");
        SeamCarver sc = new SeamCarver(p);

        int[] seam = sc.findVerticalSeam();
        System.out.println(Arrays.toString(seam));


    }
}