import com.shortn.sally.interfacing.ShellProcessor;

import java.util.Scanner;

public class ProcessTest {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        ShellProcessor process = new ShellProcessor("test", "A test of the shell processor. Enter exit, help, or test", System.out::print, input::nextLine);
        process.addVerb("test", VerbTest::new);
        process.run();

    } // end of main function
} // end of ProcessTest class
