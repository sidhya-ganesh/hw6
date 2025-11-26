import java.util.ArrayList;
import java.util.HashMap;

/**
 * EvilSolution maintains a set of possible target words of the same length and
 * a partially revealed solution. On each guess, it partitions the candidate
 * words into "word families" based on the positions of the guessed letter and
 * chooses the family that keeps the largest number of options.
 */
public class EvilSolution {

    /** All remaining candidate words consistent with guesses so far. */
    private ArrayList<String> candidates;

    /** Partially revealed solution, with '_' for unknown positions. */
    private ArrayList<Character> partialSolution;

    /** Count of characters still not revealed. */
    private int missingChars;

    /**
     * Constructs a new EvilSolution given an initial list of candidate words.
     * All words are assumed to have the same length.
     */
    public EvilSolution(ArrayList<String> wordList) {
        if (wordList == null || wordList.isEmpty()) {
            throw new IllegalArgumentException("Word list must be non-empty");
        }

        this.candidates = new ArrayList<>(wordList);
        int length = candidates.get(0).length();

        this.partialSolution = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            partialSolution.add('_');
        }
        this.missingChars = length;
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

    /**
     * Handles a user's guess in evil fashion.
     *
     * We:
     *  1. Build word families keyed by pattern (e.g. "_e__") after placing guess.
     *  2. Choose the family with the largest size.
     *  3. If there's a tie, choose the family that reveals the fewest NEW letters.
     *  4. Update candidates and partialSolution.
     *
     * @param guess letter guessed by the user
     * @return true if the guess reveals at least one new letter, false otherwise
     */
    public boolean addGuess(char guess) {
        HashMap<String, ArrayList<String>> families = new HashMap<>();

        // Partition current candidates into families.
        for (String word : candidates) {
            String pattern = buildPattern(word, guess);
            ArrayList<String> family = families.get(pattern);
            if (family == null) {
                family = new ArrayList<>();
                families.put(pattern, family);
            }
            family.add(word);
        }

        // Choose "best" family: largest size, then fewest new reveals.
        String bestPattern = null;
        ArrayList<String> bestFamily = null;
        int bestSize = -1;
        int bestNewReveals = Integer.MAX_VALUE;

        for (String pattern : families.keySet()) {
            ArrayList<String> family = families.get(pattern);
            int size = family.size();
            int newReveals = countNewReveals(pattern, guess);

            if (size > bestSize) {
                bestSize = size;
                bestPattern = pattern;
                bestFamily = family;
                bestNewReveals = newReveals;
            } else if (size == bestSize && newReveals < bestNewReveals) {
                bestPattern = pattern;
                bestFamily = family;
                bestNewReveals = newReveals;
            }
        }

        // Update candidates to chosen family.
        candidates = bestFamily;

        // Update partial solution and missingChars based on bestPattern.
        boolean guessCorrect = bestNewReveals > 0;
        if (guessCorrect) {
            for (int i = 0; i < bestPattern.length(); i++) {
                char c = bestPattern.charAt(i);
                if (c == guess && partialSolution.get(i) == '_') {
                    partialSolution.set(i, c);
                }
            }
            missingChars -= bestNewReveals;
        }

        return guessCorrect;
    }

    /**
     * Returns one valid target word consistent with all guesses so far.
     * Called at the end of the game; any remaining candidate works.
     */
    public String getTarget() {
        if (candidates.isEmpty()) {
            return "";
        }
        return candidates.get(0);
    }

    /**
     * Build a pattern string for a given word and guess, using the current
     * partialSolution as the base. Positions where the word has 'guess' become
     * 'guess'; all others stay as in partialSolution.
     */
    private String buildPattern(String word, char guess) {
        StringBuilder sb = new StringBuilder(partialSolution.size());
        for (int i = 0; i < partialSolution.size(); i++) {
            char existing = partialSolution.get(i);
            if (word.charAt(i) == guess) {
                sb.append(guess);
            } else {
                sb.append(existing);
            }
        }
        return sb.toString();
    }

    /**
     * Count how many NEW letters would be revealed if we move from the current
     * partialSolution to this pattern.
     */
    private int countNewReveals(String pattern, char guess) {
        int count = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == guess && partialSolution.get(i) == '_') {
                count++;
            }
        }
        return count;
    }
}
