public class TrieNode {
	private char character;
	private TrieNode parent;
	private TrieNode[] children;
	private boolean isLeaf;
	private boolean isWord;
		
	public TrieNode() {
		//Initialize root node
		this.character = '0';
		this.parent = null;
		this.children = new TrieNode[26];
		this.isLeaf = true;
		this.isWord = false;
	}
	
	public TrieNode(char ch, TrieNode parent) {
		this.character = ch;
		this.parent = parent;
		this.children = new TrieNode[26];
		this.isLeaf = true;
		this.isWord = false;
	}
	
	public char getLetter() {
		return character;
	}
	
	public TrieNode getParent() {
		return parent;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}
	
	public boolean isWord() {
		return isWord;
	}
	
	public TrieNode add(char ch) {
		//If ch not present in children, add it. Then return the child TrieNode
		if (children[ch - 97] == null) {
			children[ch - 97] = new TrieNode(ch, this);
			isLeaf = false;
		}
		return children[ch - 97];
	}
	
	public void setWord() {
		isWord = true;
	}
	
	public TrieNode getNextTrieNode(char ch) {
		return children[(int)ch - 97];
	}
	
	public void printAllWords(String prefix) {
		//For testing purposes
		if (isWord) {
			System.out.println(prefix + String.valueOf(character));
		}
		if (isLeaf) {
			return;
		}
		for (int i = 0; i < children.length; i++) {
			if (children[i] != null) {
				if (parent == null) {
					children[i].printAllWords(prefix);
				} else {
					children[i].printAllWords(prefix + String.valueOf(character));
				}
			}
		}
	}
	
	
	
}