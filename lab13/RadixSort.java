/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra
 * @version 1.4 - April 14, 2016
 *
 **/

import java.util.Arrays;

public class RadixSort
{

    /**
     * Does Radix sort on the passed in array with the following restrictions:
     *  The array can only have ASCII Strings (sequence of 1 byte characters)
     *  The sorting is stable and non-destructive
     *  The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     **/
    public static String[] sort(String[] asciis)
    {
        String[] sorted = new String[asciis.length];
        System.arraycopy(asciis, 0, sorted, 0, asciis.length);
        sortHelper(sorted, 0, sorted.length, 0);
        return sorted;
    }

    /**
     * Radix sort helper function that recursively calls itself to achieve the sorted array
     *  destructive method that changes the passed in array, asciis
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelper(String[] asciis, int start, int end, int index)
    {
        if (start == end || (end - start == 1)) {
            return;
        }
        // gather counts for each value
        int[] counts = new int[256];
        int pointer = start;
        while (pointer < end) {
            if (index >= asciis[pointer].length()) {
                counts[0] += 1;
            } else {
                counts[(int) asciis[pointer].charAt(index)] += 1;
            }
            pointer += 1;
        }
        
        //update the counts for the new positions
        for (int i = 1; i < 256; i++) {
            counts[i] += counts[i - 1];
        }
        
        // sort into temp array
        String[] temp = new String[end - start];
        while (pointer > start) {
            pointer -= 1;
            int pos;
            if (index >= asciis[pointer].length()) {
                counts[0] -= 1;
                pos = counts[0];
            } else {
                counts[(int) asciis[pointer].charAt(index)] -= 1;
                pos = counts[(int) asciis[pointer].charAt(index)];
            }
            temp[pos] = asciis[pointer];
        }
        
        
        //copy back into original array
        System.arraycopy(temp, 0, asciis, start, temp.length);
        
        //recursively sort based on the next index
        for (int i = 1; i < counts.length; i++) {
            sortHelper(asciis, start + counts[i - 1], start +counts[i], index + 1);
        }
        sortHelper(asciis, start + counts[counts.length - 1], end, index + 1);
    }
    
    public static void main(String[] args) {
        String[] students = {"Alice", "Vanessa", "Ethan", "Amylyn", "Aragorn", "Amanda", "Jeff", "Jeffrey", "Notch", "Amy"};
        //String[] students = {"Alice", "Vanessa", "Ethan", "Amy"};
        
        String[] sortedStudents = sort(students);
        System.out.println("Original array:");
        System.out.println(Arrays.toString(students));
        System.out.println("Sorted array:");
        System.out.println(Arrays.toString(sortedStudents));
    }
}
