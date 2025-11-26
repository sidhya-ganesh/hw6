import java.util.ArrayList;

public class Solution {

	private String target;
	private ArrayList<Character> partialSolution;
	private int missingChars;

	public Solution(String target) {
		this.target = target;
		missingChars = target.length();
		partialSolution = new ArrayList<>(missingChars);
		for (int i = 0; i < target.length(); i++) {
			partialSolution.add('_');
		}
	}

	public boolean isSolved() {
		return missingChars == 0;
	}

	public void printProgress() {
		for (char c : partialSolution) {
			System.out.print(c + " ");
		}
		System.out.println();
	}

	public boolean addGuess(char guess) {
		boolean guessCorrect = false;
		for (int i = 0; i < target.length(); i++) {
			if (target.charAt(i) == guess) {
				partialSolution.set(i, guess);
				missingChars--;
				guessCorrect = true;
			}
		}
		return guessCorrect;
	}

	public String getTarget() {
		return target;
	}
}
