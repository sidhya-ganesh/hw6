import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;

public class EvilHangman {

    private ArrayList<String> wordList;
    private HashSet<Character> previousGuesses;
    private TreeSet<Character> incorrectGuesses; // behaves like a hash set, but orders the entries!
    private EvilSolution solution;
    private Scanner inputReader;

    /**
     * Default constructor – uses the same dictionary file as the original Hangman.
     */
    public EvilHangman() {
        this("engDictionary.txt");
    }

    /**
     * Main constructor that mirrors Hangman(String filename), but sets up
     * an EvilSolution instead of a fixed Solution.
     */
    public EvilHangman(String filename) {
        try {
            wordList = dictionaryToList(filename);
        } catch (IOException e) {
            System.out.printf(
                    "Couldn't read from the file %s. Verify that you have it in the right place and try running again.",
                    filename);
            e.printStackTrace();
            System.exit(0); // stop the program--no point in trying if you don't have a dictionary
        }

        previousGuesses = new HashSet<>();
        incorrectGuesses = new TreeSet<>();

        // Choose a random word just to decide the length we will play with.
        int randomIndex = new Random().nextInt(wordList.size());
        int targetLength = wordList.get(randomIndex).length();

        // Collect all words of that length to form the initial evil candidate pool.
        ArrayList<String> candidates = new ArrayList<>();
        for (String word : wordList) {
            if (word.length() == targetLength) {
                candidates.add(word);
            }
        }

        solution = new EvilSolution(candidates);
        inputReader = new Scanner(System.in);
    }

    /**
     * Extra constructor used for unit testing: lets tests pass a custom dictionary
     * and Scanner. Here we simply use the length of the first word as the
     * target length to keep behavior deterministic for tests.
     */
    public EvilHangman(ArrayList<String> dictionary, Scanner input, Random random) {
        this.wordList = new ArrayList<>(dictionary);
        this.previousGuesses = new HashSet<>();
        this.incorrectGuesses = new TreeSet<>();

        int targetLength = wordList.get(0).length(); // deterministic for tests

        ArrayList<String> candidates = new ArrayList<>();
        for (String word : wordList) {
            if (word.length() == targetLength) {
                candidates.add(word);
            }
        }

        this.solution = new EvilSolution(candidates);
        this.inputReader = input;
    }

    public void start() {
        while (!solution.isSolved()) {
            char guess = promptForGuess();
            recordGuess(guess);
        }
        printVictory();
    }

    private char promptForGuess() {
        while (true) {
            System.out.println("Guess a letter.\n");
            solution.printProgress();
            System.out.println("Incorrect guesses:\n" + incorrectGuesses.toString());
            String input = inputReader.next();
            if (input.length() != 1) {
                System.out.println("Please enter a single character.");
            } else if (previousGuesses.contains(input.charAt(0))) {
                System.out.println("You've already guessed that.");
            } else if (!Character.isAlphabetic(input.charAt(0))) {
                System.out.println("Please enter an alphabetic character.");
            } else {
                return input.charAt(0);
            }
        }
    }

    private void recordGuess(char guess) {
        previousGuesses.add(guess);
        boolean isCorrect = solution.addGuess(guess);
        if (!isCorrect) {
            incorrectGuesses.add(guess);
        }
    }

    private void printVictory() {
        System.out.printf("Congrats! The word was %s%n", solution.getTarget());
    }

    private static ArrayList<String> dictionaryToList(String filename) throws IOException {
        FileInputStream fileInput = new FileInputStream(filename);
        Scanner fileReader = new Scanner(fileInput);

        ArrayList<String> wordList = new ArrayList<>();

        while (fileReader.hasNext()) {
            wordList.add(fileReader.next());
        }

        // Starter code didn't close these; behavior is identical either way.
        // fileReader.close();
        // fileInput.close();

        return wordList;
    }
}
