import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Tests for EvilHangman.start(), focusing on user I/O behavior.
 */
public class EvilHangmanTest {

    private EvilHangman buildGame(String dictionaryWord, String simulatedInput) {
        ArrayList<String> dict = new ArrayList<>();
        dict.add(dictionaryWord);

        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(in);

        Random random = new Random(0);

        return new EvilHangman(dict, scanner, random);
    }

    @Test
    public void testStartHandlesInvalidInputThenValid() {
        EvilHangman game = buildGame("cat", "12\nc\na\nt\n");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        game.start();

        System.setOut(original);

        String output = out.toString();
        assertTrue(output.contains("Please enter a single character."));
        assertTrue(output.contains("Congrats! The word was cat"));
    }

    @Test
    public void testStartRejectsRepeatedGuess() {
        EvilHangman game = buildGame("dog", "d\nd\no\ng\n");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        game.start();

        System.setOut(original);

        String output = out.toString();
        assertTrue(output.contains("You've already guessed that."));
        assertTrue(output.contains("Congrats! The word was dog"));
    }

    @Test
    public void testStartTracksIncorrectGuesses() {
        EvilHangman game = buildGame("a", "b\na\n");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        game.start();

        System.setOut(original);

        String output = out.toString();
        assertTrue(output.contains("Incorrect guesses:\n[b]"));
        assertTrue(output.contains("Congrats! The word was a"));
    }
}
