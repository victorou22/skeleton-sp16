package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void dequeueTest1() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<Integer>(3);
        arb.enqueue(5);
        arb.enqueue(6);
        assertEquals(5, (int) arb.dequeue());
        arb.enqueue(7);
        arb.enqueue(8);
        arb.enqueue(9);
        assertEquals(6, (int) arb.dequeue());
        assertEquals(7, (int) arb.dequeue());
    }

    @Test
    public void dequeueTest2() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<Integer>(3);
        arb.enqueue(5);
        arb.enqueue(6);
        arb.enqueue(7);
        assertEquals(5, (int) arb.peek());
        assertEquals(5, (int) arb.peek());
        assertEquals(5, (int) arb.dequeue());
        assertEquals(6, (int) arb.peek());
        assertEquals(6, (int) arb.dequeue());
        assertEquals(7, (int) arb.dequeue());
        assertEquals(null, arb.peek());
        arb.enqueue(9);
        arb.enqueue(10);
        arb.enqueue(11);
        assertEquals(9, (int) arb.dequeue());
        assertEquals(10, (int) arb.dequeue());
        assertEquals(11, (int) arb.dequeue());
    }

    @Test
    public void printTest() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<Integer>(3);
        arb.enqueue(5);
        arb.enqueue(6);
        arb.enqueue(7);
        System.out.println("hello");
        for (Integer i : arb) {
            System.out.println(i);
        }
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
