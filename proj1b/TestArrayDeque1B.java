import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDeque1B {
	@Test
	public void randomTests() {
		StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<Integer>();
		ArrayDequeSolution<Integer> sad2 = new ArrayDequeSolution<Integer>();
		FailureSequence fs = new FailureSequence();

		while(true) {
			int item = StdRandom.uniform(20);
			switch(StdRandom.uniform(6)) {
				case 0: {
					DequeOperation do0 = new DequeOperation("addFirst", item);
					fs.addOperation(do0);
					sad2.addFirst(item);
					sad1.addFirst(item);
					break;
				}
				case 1: {
					DequeOperation do1 = new DequeOperation("addLast", item);
					fs.addOperation(do1);
					sad2.addLast(item);
					sad1.addLast(item);
					break;
				}
				case 2: {
					DequeOperation do2 = new DequeOperation("isEmpty");
					fs.addOperation(do2);
					assertEquals(fs.toString(), sad2.isEmpty(), sad1.isEmpty());
					break;
				}
				case 3: {
					DequeOperation do3 = new DequeOperation("size");
					fs.addOperation(do3);
					assertEquals(fs.toString(), sad2.size(), sad1.size());
					break;
				}
				case 4: {
					DequeOperation do4 = new DequeOperation("removeFirst");
					fs.addOperation(do4);
					assertEquals(fs.toString(), sad2.removeFirst(), sad1.removeFirst());
					break;
				}
				case 5: {
					DequeOperation do5 = new DequeOperation("removeLast");
					fs.addOperation(do5);
					assertEquals(fs.toString(), sad2.removeLast(), sad1.removeLast());
					break;
				}
				case 6: {
					int randGet = StdRandom.uniform(sad2.size());
					DequeOperation do6 = new DequeOperation("get", randGet);
					fs.addOperation(do6);
					assertEquals(fs.toString(), sad2.get(randGet), sad1.get(randGet));
					break;
				}

			}
		}
	}
	
	public static void main(String... args) {        
        jh61b.junit.TestRunner.runTests("all", TestArrayDeque1B.class);
    }
}