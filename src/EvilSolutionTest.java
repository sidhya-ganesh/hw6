import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit tests for EvilSolution.
 */
public class EvilSolutionTest {

    private EvilSolution buildSolution(String... words) {
        ArrayList<String> list = new ArrayList<>();
        for (String w : words) {
            list.add(w);
        }
        return new EvilSolution(list);
    }

    @Test
    public void testIsSolvedInitiallyFalse() {
        EvilSolution sol = buildSolution("echo", "heal");
        assertFalse(sol.isSolved());
    }

    @Test
    public void testAddGuessCreatesExpectedFamily() {
        // Example dictionary
        EvilSolution sol = buildSolution("echo", "heal", "belt", "peel", "hazy");

        sol.addGuess('e');

        // After guessing 'e', largest family is pattern "_e__" (heal, belt).
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        sol.printProgress();

        System.setOut(original);

        String progress = out.toString().trim();
        String compact = progress.replace(" ", "");
        assertEquals("_e__", compact);
    }

    @Test
    public void testAddGuessCanBeIncorrect() {
        EvilSolution sol = buildSolution("echo", "heal");

        boolean correct = sol.addGuess('z');
        assertFalse(correct);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        sol.printProgress();

        System.setOut(original);

        String progress = out.toString().trim();
        String compact = progress.replace(" ", "");
        assertEquals("____", compact);
    }

    @Test
    public void testMultipleGuessesUpdatePattern() {
        EvilSolution sol = buildSolution("peel", "feel");

        sol.addGuess('e');
        sol.addGuess('l');

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        sol.printProgress();

        System.setOut(original);

        String progress = out.toString().trim();
        String compact = progress.replace(" ", "");
        assertEquals("_eel", compact);
    }

    @Test
    public void testGetTargetNotEmpty() {
        EvilSolution sol = buildSolution("echo", "heal");
        String target = sol.getTarget();
        assertNotNull(target);
        assertTrue(target.length() > 0);
    }
}
