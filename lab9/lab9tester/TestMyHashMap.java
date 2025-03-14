package lab9;
import static org.junit.Assert.*;
import org.junit.Test;
import lab9.MyHashMap;
import java.util.HashSet;
import java.util.Set;

/** Tests by Brendan Hu, Spring 2015, revised for 2016 by Josh Hug */
public class TestMyHashMap {

	@Test
    public void sanityGenericsTest() {
    	try {
    		MyHashMap<String, String> a = new MyHashMap<String, String>();
	    	MyHashMap<String, Integer> b = new MyHashMap<String, Integer>();
	    	MyHashMap<Integer, String> c = new MyHashMap<Integer, String>();
	    	MyHashMap<Boolean, Integer> e = new MyHashMap<Boolean, Integer>();
	    } catch (Exception e) { 
	    	fail();
	    }
    }

    //assumes put/size/containsKey/get work
	@Test
    public void sanityClearTest() {
    	MyHashMap<String, Integer> b = new MyHashMap<String, Integer>();
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1);
            //make sure put is working via containsKey and get
            assertTrue( null != b.get("hi" + i)
                        && b.containsKey("hi" + i)); 
        }
        b.clear();
        assertEquals(0, b.size());
        for (int i = 0; i < 455; i++) {
            assertTrue(null == b.get("hi" + i) && !b.containsKey("hi" + i));
        }
    }

    // assumes put works
    @Test
    public void sanityContainsKeyTest() {
    	MyHashMap<String, Integer> b = new MyHashMap<String, Integer>();
        assertFalse(b.containsKey("waterYouDoingHere"));
        b.put("waterYouDoingHere", 0);
        assertTrue(b.containsKey("waterYouDoingHere"));
    }

    // assumes put works
    @Test
    public void sanityGetTest() {
    	MyHashMap<String, Integer> b = new MyHashMap<String, Integer>();
        assertEquals(null,b.get("starChild"));
        b.put("starChild", 5);
        assertNotEquals(null,b.get("starChild"));
        b.put("KISS", 5);
        assertNotEquals(null,b.get("KISS"));
        assertNotEquals(null,b.get("starChild"));
    }

    // assumes put works
    @Test
    public void sanitySizeTest() {
    	MyHashMap<String, Integer> b = new MyHashMap<String, Integer>();
        assertEquals(0, b.size());
        b.put("hi", 1);
        assertEquals(1, b.size());
        for (int i = 0; i < 455; i++)
            b.put("hi" + i, 1);
        assertEquals(456, b.size());
    }

    //assumes get/containskey work
    @Test
    public void sanityPutTest() {
    	MyHashMap<String, Integer> b = new MyHashMap<String, Integer>();
        b.put("hi", 1);
        assertTrue(b.containsKey("hi") && b.get("hi") != null);
    }

    /* 
    * Sanity test for keySet
    */
    @Test
    public void sanityKeySetTest() {
    	MyHashMap<String, Integer> b = new MyHashMap<String, Integer>();
        HashSet<String> values = new HashSet<String>();
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1);   
            values.add("hi" + i);
        }
        assertEquals(455, b.size()); //keys are there
        Set<String> keySet = b.keySet();
        assertTrue(values.containsAll(keySet));
        assertTrue(keySet.containsAll(values));
    }

    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests(TestMyHashMap.class);
    }
}
