package lab9;

import java.util.*;

public class MyHashMap<K, V> implements Map61B<K, V> {
    private int capacity;
    private double loadFactor;
    private ArrayList<LinkedList<Entry<K, V>>> keys;
    private HashSet<K> keySet;
    
    private class Entry<K, V> {
        private final K key;
        private final V val;
        
        public Entry(K key, V val) {
            this.key = key;
            this.val = val;
        }
        
        public K getKey() {
            return key;
        }
        
        public V getVal() {
            return val;
        }
    }
    
    public MyHashMap() {
        capacity = 16;
        loadFactor = 0.5;
        keys = new ArrayList<LinkedList<Entry<K, V>>>(Collections.nCopies(16, null));
        keySet = new HashSet<K>();
    }
    
    public MyHashMap(int initialSize) {
        capacity = initialSize;
        loadFactor = 0.5;
        keys = new ArrayList<LinkedList<Entry<K, V>>>(Collections.nCopies(initialSize, null));
        keySet = new HashSet<K>();
    }
    
    public MyHashMap(int initialSize, double loadFactor) {
        capacity = initialSize;
        this.loadFactor = loadFactor;
        keys = new ArrayList<LinkedList<Entry<K, V>>>(Collections.nCopies(initialSize, null));
        keySet = new HashSet<K>();
    }
    
    @Override
    public void clear() {
        keySet.clear();
        keys = new ArrayList<LinkedList<Entry<K, V>>>(Collections.nCopies(capacity, null));
    }
    
    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }
    
    @Override
    public V get(K key) {
        if (!keySet.contains(key)) {
            return null;
        }
        int hashedKey = Math.abs(key.hashCode() % capacity);
        LinkedList<Entry<K, V>> ll = keys.get(hashedKey);   
        if (ll != null) {     
            for (Entry<K, V> e : ll) {  
                if (key.equals(e.getKey())) {
                    return e.getVal();
                }
            }
        }
        return null;
    }
    
    @Override
    public int size() {
        return keySet.size();
    }
    
    private void resize() {
        capacity *= 2;
        ArrayList<LinkedList<Entry<K, V>>> newKeys = new ArrayList<LinkedList<Entry<K, V>>>(Collections.nCopies(capacity, null));
            for (LinkedList<Entry<K, V>> ell : keys) {
                if (ell != null) {
                    for (Entry<K, V> e : ell) {
                        int hashedKey = Math.abs(e.getKey().hashCode() % capacity);
                        LinkedList<Entry<K, V>> newValList = newKeys.get(hashedKey);
                        if (newValList == null) {
                            newValList = new LinkedList<Entry<K, V>>();
                            newValList.add(new Entry<K, V>(e.getKey(), e.getVal()));
                            newKeys.set(hashedKey, newValList);
                        } else {
                            newValList.add(new Entry<K, V>(e.getKey(), e.getVal()));
                        }
                    }
                }
            }
        keys = newKeys;
    }
    
    @Override
    public void put(K key, V val) {
        int hashedKey = Math.abs(key.hashCode() % capacity);
        LinkedList<Entry<K, V>> valList = keys.get(hashedKey);
        if (valList == null) {
            valList = new LinkedList<Entry<K, V>>();
            valList.add(new Entry<K, V>(key, val));
            keys.set(hashedKey, valList);
            keySet.add(key);
        } else {
            valList.add(new Entry<K, V>(key, val));
            keySet.add(key);
        }
        if (((double) keySet.size()/(double) capacity) >= loadFactor) {
            this.resize();     
        }
    }
    
    public void print() {
        for (LinkedList<Entry<K, V>> lle : keys) {
            if (lle != null) {
                for (Entry<K, V> e : lle) {
                    System.out.println(e.getKey() + " " + e.getVal());
                }
            }
        }
        System.out.println("Break");
    }
    
    @Override
    public Set<K> keySet() {
        return keySet;
    }
    
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public V remove(K key, V val) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterator<K> iterator() {
        return null;
    }
    
    public static void main(String args[]) {
        MyHashMap<String, Integer> b = new MyHashMap<String, Integer>();
        for (int i = 0; i < 8; i++) {
            b.put("hi" + i, 1);
            //make sure put is working via containsKey and get
            System.out.println(b.get("hi" + i));
            //System.out.println(b.containsKey("hi" + i)); 
        }
    }
}