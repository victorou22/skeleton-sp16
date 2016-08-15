public class Palindrome {
	public static Deque<Character> wordToDeque(String word) {
		if (word.isEmpty()) {
			return new LinkedListDequeSolution<Character>();
		}
		Deque<Character> dc = wordToDeque(word.substring(1));
		dc.addFirst(word.charAt(0));
		return dc;
	}

	private static boolean isPalindromeHelper(Deque<Character> deq) {
		if (deq.isEmpty() || deq.size() == 1) {
			return true;
		}
		else if(deq.removeFirst() != deq.removeLast()) {
			return false;
		}
		return isPalindromeHelper(deq);
	}

	public static boolean isPalindrome(String word) {
		return isPalindromeHelper(wordToDeque(word));
	}

	private static boolean isPalindromeHelper(Deque<Character> deq, CharacterComparator cc) {
		if (deq.isEmpty() || deq.size() == 1) {
			return true;
		}
		else if (!cc.equalChars(deq.removeFirst(), deq.removeLast())) {
			return false;
		}
		return isPalindromeHelper(deq, cc);
	}

	public static boolean isPalindrome(String word, CharacterComparator cc) {
		return isPalindromeHelper(wordToDeque(word), cc);
	}
}