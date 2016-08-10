public class ArrayDeque<Item> {

	private Item[] items;
	private int size;
	private int maxSize;
	private int nextFirst;
	private int nextLast;

	public ArrayDeque() {
		size = 0;
		items = (Item[]) new Object[8];
		maxSize = 8;
		nextFirst = 3;
		nextLast = 4;
	}

	private int getPrevIndex(int index) {
		if(index == 0)
			return maxSize - 1;
		return --index;
	}

	private int getNextIndex(int index) {
		if(index == maxSize - 1)
			return 0;
		return ++index;
	}

	private double usageFactor() {
		return (double) size / maxSize;
	}

	private boolean needToGrowArray() {
		return (usageFactor() >= 0.75) ? true : false;
	}

	private void growArray() {
		Item[] newItems = (Item[]) new Object[maxSize*2];
		for(int i = 0, toBeCopied = getNextIndex(nextFirst); i < size; i++) {
			newItems[i] = items[toBeCopied];
			toBeCopied = getNextIndex(toBeCopied);
		}
		items = newItems;
		maxSize = maxSize*2;
		nextFirst = maxSize - 1;
		nextLast = size;
	}

	private boolean needToShrinkArray() {
		return (usageFactor() <= 0.25) ? true : false;
	}

	private void shrinkArray() {
		Item[] newItems = (Item[]) new Object[maxSize/2];
		for(int i = 0, toBeCopied = getNextIndex(nextFirst); i < size; i++) {
			newItems[i] = items[toBeCopied];
			toBeCopied = getNextIndex(toBeCopied);
		}
		items = newItems;
		maxSize = maxSize/2;
		nextFirst = maxSize - 1;
		nextLast = size;
	}

	public void addFirst(Item obj) {
		if(needToGrowArray())
			growArray();
		items[nextFirst] = obj;
		nextFirst = getPrevIndex(nextFirst);
		size++;
	}

	public void addLast(Item obj) {
		if(needToGrowArray())
			growArray();
		items[nextLast] = obj;
		nextLast = getNextIndex(nextLast);;
		size++;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int size() {
		return size;
	}

	public void printDeque() {
		int currIndex = getNextIndex(nextFirst);
		for(int i = 0; i < size; i++) {
			System.out.println(items[currIndex]);
			currIndex = getNextIndex(currIndex);
		}
	}

	public Item removeFirst() {
		if(size == 0)
			return null;
		if(needToShrinkArray())
			shrinkArray();
		nextFirst = getNextIndex(nextFirst);
		size--;
		return items[nextFirst];
	}

	public Item removeLast() {
		if(size == 0)
			return null;
		if(needToShrinkArray())
			shrinkArray();
		nextLast = getPrevIndex(nextLast);
		size--;
		return items[nextLast];
	}

	private int getIndex(int index) {
		//calculates the index in the circular array for get()
		if(nextFirst + index  + 1 >= maxSize)
			return nextFirst + index + 1 - maxSize;
		return nextFirst + index + 1;
	}

	public Item get(int index) {
		if(size == 0 || index < 0 || index >= size)
			return null;
		return items[getIndex(index)];
	}
}