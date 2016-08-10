public class LinkedListDeque<Item> {

	public class Node {
		public Item item;
		public Node next;
		public Node prev;
	}

	private int size;
	private Node sentinel;

	public LinkedListDeque() {
		size = 0;
		sentinel = new Node();
		sentinel.next = sentinel;
		sentinel.prev = sentinel;
	}

	public int size() {
		return size;
	}

	public void addFirst(Item obj) {
		Node tail = sentinel.next;
		sentinel.next = new Node();
		sentinel.next.item = obj;
		sentinel.next.next = tail;
		sentinel.next.prev = sentinel;
		size++;
	}

	public void addLast(Item obj) {
		Node tail = sentinel.prev;
		sentinel.prev = new Node();
		sentinel.prev.item = obj;
		sentinel.prev.prev = tail;
		sentinel.prev.next = sentinel;
		size++;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void printDeque() {
		Node runner = sentinel.next;
		while(runner != sentinel) {
			System.out.println(runner.item);
			runner = runner.next;
		}
	}

	public Item removeFirst() {
		if(size == 0)
			return null;
		Item obj = sentinel.next.item;
		sentinel.next = sentinel.next.next;
		size--;
		return obj;
	}

	public Item removeLast() {
		if(size == 0)
			return null;
		Item obj = sentinel.prev.item;
		sentinel.prev = sentinel.prev.prev;
		size--;
		return obj;
	}

	public Item get(int index) {
		if(size == 0 || index < 0 || index >= size)
			return null;
		Node runner = sentinel;
		while(index >= 0) {
			runner = runner.next;
			index--;
		}
		return runner.item;
	}

	public Item getRecursive(int index) {
		return getRecursive(index, sentinel.next);
	}

	private Item getRecursive(int index, Node head) {
		if(index == 0)
			return head.item;
		return getRecursive(index - 1, head.next);
	}
}