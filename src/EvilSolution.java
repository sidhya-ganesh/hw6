import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class EvilSolution {

    // Current list of all the possible target words - all same length
    private ArrayList<String> candidates;

    // partially revealed solution, using '_' sign for any unknown characters
    private ArrayList<Character> partialSolution;

    // num of characters in the word that have not yet been revealed
    private int missingChars;

    // constructs a new EvilSolution from an initial list of words.
    // the list may contain words of many different lengths.
    // this constructor will:
       // 1) Choose a random word from the list
       // 2) Use that word's length as the chosen length
       // 3) Keep only words of that length as the candidate set

    public EvilSolution(ArrayList<String> wordList) {
        if (wordList == null || wordList.isEmpty()) {
            throw new IllegalArgumentException("Word list must be non-empty");
        }

        // 1) Pick a random word: length becomes the chosen length
        Random rand = new Random();
        String randomWord = wordList.get(rand.nextInt(wordList.size()));
        int chosenLength = randomWord.length();

        // 2) filter to only words of that length
        this.candidates = new ArrayList<>();
        for (String w : wordList) {
            if (w.length() == chosenLength) {
                candidates.add(w);
            }
        }

        // 3) initialize partial solution: "_ _ _ ..."
        this.partialSolution = new ArrayList<>(chosenLength);
        for (int i = 0; i < chosenLength; i++) {
            partialSolution.add('_');
        }

        this.missingChars = chosenLength;
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

    // Processes a user's guess in "evil" fashion.
     // Partition the candidate words into families based on where 'guess' appears.
     // Choose the largest family.
     // Break ties by revealing the fewest new letters.
     // Update candidates and partialSolution accordingly.
     // guess the character guessed by the user
     // return true if at least one new letter is revealed, false otherwise

    public boolean addGuess(char guess) {
        HashMap<String, ArrayList<String>> families = new HashMap<>();

        // Build word families w key of pattern
        for (String word : candidates) {
            String pattern = buildPattern(word, guess);
            ArrayList<String> family = families.get(pattern);
            if (family == null) {
                family = new ArrayList<>();
                families.put(pattern, family);
            }
            family.add(word);
        }

        // choose best family -  largest size, then fewest new reveals
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

        // update candidates to the chosen family
        candidates = bestFamily;

        // if no new positions revealed, the guess is incorrect
        if (bestNewReveals == 0) {
            return false;
        }

        // Update partialSolution and missingChars
        for (int i = 0; i < bestPattern.length(); i++) {
            char c = bestPattern.charAt(i);
            if (c == guess && partialSolution.get(i) == '_') {
                partialSolution.set(i, c);
            }
        }
        missingChars -= bestNewReveals;
        return true;
    }

    //Returns a single target word from the remaining candidates.
    //Any remaining candidate is valid, since they all match all guesses so far.
    public String getTarget() {
        if (candidates.isEmpty()) {
            return "";
        }
        return candidates.get(0);
    }

    // Builds a pattern string for a candidate word given the current partialSolution
    // and a guessed character. Positions where the word has 'guess' are filled
    // with 'guess', and other positions use the already-known character or '_'

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

    //Counts how many new letters coul be revealed by adopting the given pattern
    private int countNewReveals(String pattern, char guess) {
        int count = 0;
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == guess && partialSolution.get(i) == '_') {
                count++;
            }
        }
        return count;
    }
}
