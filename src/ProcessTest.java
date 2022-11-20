import com.shortn.sally.interfacing.ShellProcessor;

import java.util.Scanner;

public class ProcessTest {
    public static void main(String[] args) {

        ShellProcessor process = new ShellProcessor("test", "A test of the shell processor. Enter exit, help, or test");
        process.addVerb("test", VerbTest::new);
        process.run();

    } // end of main function
} // end of ProcessTest class
