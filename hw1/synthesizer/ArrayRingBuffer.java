package synthesizer;
import java.util.Iterator;

public class ArrayRingBuffer<T> extends AbstractBoundedQueue<T> {
    /* Index for the next dequeue or peek. */
    private int first;
    /* Index for the next enqueue. */
    private int last;
    /* Array for storing the buffer data. */
    private T[] rb;


    public ArrayRingBuffer(int capacity) {
        rb = (T[]) new Object[capacity];
        first = 0;
        last = 0;
        fillCount = 0;
        this.capacity = capacity;
    }

    @Override
    public void enqueue(T x) {
        try {   //if ring buffer full, throw exception
            if(fillCount == capacity) {
                throw new RuntimeException("Ring buffer overflow");
            }
            rb[last] = x;
            fillCount++;
            if (last == capacity - 1) {
                last = 0;
            } else {
                last++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public T dequeue() { 
        T ret = null;
        try {   //if ring buffer empty, throw exception
            if (fillCount == 0) {
                throw new RuntimeException("Ring buffer underflow");
            }
            ret = rb[first];
            fillCount--;
            if (first == capacity - 1) {
                first = 0;
            } else {
                first++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }

    /**
     * Return oldest item, but don't remove it.
     */
    @Override
    public T peek() {
        if (fillCount == 0) {
            return null;
        }
        return rb[first];
    }

    private class Iter implements Iterator<T> {
        private int index;
        private int counter;

        public Iter() {
            index = first;
            counter = 0;
        }

        public boolean hasNext() {
            return (counter < fillCount);
        }

        public T next() {
            T curr = rb[index];
            if(index + 1 == capacity) {
                index = 0;
            } else {
                index++;
            }
            counter++;
            return curr;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iter();
    }
}
