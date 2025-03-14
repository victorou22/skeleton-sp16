public class LetterNode {
	private char ch;
	private int x;
	private int y;
	
	public LetterNode(char ch, int x, int y) {
		this.ch = ch;
		this.x = x;
		this.y = y;
	}
	
	public char getLetter() {
		return ch;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}