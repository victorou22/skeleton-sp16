public class OffByN implements CharacterComparator {
	private int N;

	public OffByN(int N) {
		this.N = N;
	}

	public boolean equalChars(char x, char y) {
		if (x - y == N || x - y == -1*N) {
			return true;
		}
		else {
			return false;
		}
	}
}