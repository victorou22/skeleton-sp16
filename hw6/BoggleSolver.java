import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.HashSet;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.io.IOException;

public class BoggleSolver {
	/* Stores words in a trie-based dictionary 
		Supports command line arguments:
			-k Print top k results
			-d Path to dictionary
	*/
	private static String DEFAULT_DICT = "words1.txt";
	private static int DEFAULT_K = 5;
	private TrieNode root;
	private StringBuilder prefix;
	private ArrayList<String> board;
	private ArrayList<String> words;
	private boolean[][] visited;
	
	public BoggleSolver(String[] args) {
		try {
			parseArgs(args);
		} catch (IllegalArgumentException e) {
			System.out.println("Illegal argument entered.");
			System.exit(0);
		}
		root = new TrieNode();
		prefix = new StringBuilder();
		board = new ArrayList<String>();
        words = new ArrayList<String>();
		createTrie(DEFAULT_DICT);
		parseBoard();
		visited = new boolean[board.size()][board.get(0).length()];
	}
	
	private void parseArgs(String[] args) {
		//Parse arguments and parameters
		for (int i = 0; i < args.length; i+=2) {
			if (args[i].charAt(0) != '-') {
				throw new IllegalArgumentException();
			} else if (args[i].equals("-k")) {
				try {
					DEFAULT_K = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException();
				}
			} else if (args[i].equals("-d")) {
				DEFAULT_DICT = args[i + 1];
			}
		}
	}
	
	private void parseBoard() {
		//Parses and stores the input boggle board
		Scanner sc = new Scanner(System.in);
		while(sc.hasNext()) {
			String line = sc.next();
			//System.out.print();
			board.add(line);
		}
	}
	
	private void printBoard() {
		//For testing purposes
		System.out.println(board.toString());
	}
	
	private void createTrie(String dictionaryFile) {
		//Returns root of new trie after creation
		List<String> words = new ArrayList<>();
		try {
			words = Files.readAllLines(Paths.get(dictionaryFile), Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println("File read error.");
		}
		for (String word : words) {
			TrieNode currNode = root;
			for (int i = 0; i < word.length(); i++) {
				currNode = currNode.add(word.charAt(i)); 
			}
			currNode.setWord();
		}
	}
	
	private void validateAndAddNeighbor(int x, int y, ArrayList<LetterNode> neighbors) {
		if (x < 0 || x >= board.size() || y < 0 || y >= board.get(0).length()) {
			return;
		}
		if (visited[x][y]) {
			return;
		}
		neighbors.add(new LetterNode(board.get(x).charAt(y), x, y));
	}
	
	private ArrayList<LetterNode> getValidNeighbors(int x, int y) {
		ArrayList<LetterNode> neighbors = new ArrayList<>();
		validateAndAddNeighbor(x - 1, y - 1, neighbors);
		validateAndAddNeighbor(x, y - 1, neighbors);
		validateAndAddNeighbor(x + 1, y - 1, neighbors);
		validateAndAddNeighbor(x - 1, y, neighbors);
		validateAndAddNeighbor(x + 1, y, neighbors);
		validateAndAddNeighbor(x - 1, y + 1, neighbors);
		validateAndAddNeighbor(x, y + 1, neighbors);
		validateAndAddNeighbor(x + 1, y + 1, neighbors);
		return neighbors;
	}
	
	private void search(LetterNode currLetterNode, TrieNode currTrieNode) {
		/* Takes the input char and searches for words in trie
			Found words stored in ArrayList<String> words
		*/
		Stack<LetterNode> st = new Stack<>();
		st.push(currLetterNode);
		while (!st.empty()) {
			currLetterNode = st.pop();
            System.out.println(currLetterNode.getLetter() + ": " + String.valueOf(currLetterNode.getX()) + ", " + String.valueOf(currLetterNode.getY()));
            currTrieNode = currTrieNode.getNextTrieNode(currLetterNode.getLetter());
            if (currTrieNode == null) {
                continue;
            }
            char letter = currLetterNode.getLetter();
            int x = currLetterNode.getX();
            int y = currLetterNode.getY();
            prefix.append(letter);
            visited[x][y] = true;
            if (currTrieNode.isWord()) {
                words.add(prefix.toString());
            }
			if (currTrieNode.isLeaf()) { 
				prefix.deleteCharAt(prefix.length() - 1);
                currTrieNode = currTrieNode.getParent();
				visited[x][y] = false;
				continue;
			}
			
			ArrayList<LetterNode> neighbors = getValidNeighbors(x, y);
			for (LetterNode neighbor : neighbors) {
				st.push(neighbor);
			}
		}
	}
	
	public void solve() {
		for (int i = 0; i < board.size(); i++) {
			for (int j = 0; j < board.get(0).length(); j++) {
				char ch = board.get(i).charAt(j);
				search(new LetterNode(ch, i, j), root);
			}
		}
	}
	
	public void printWords() {
		System.out.println(words.toString());
	}
	
	public static void main(String[] args) {
		BoggleSolver solver = new BoggleSolver(args);
		solver.solve();
		solver.printWords();
	}
}