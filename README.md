# Sally
Java framework to allow easy creation of shell-based applications.
## Including in your project
You can download the JAR file of all needed resources, as well as the Javadoc for the framework. 
Add that JAR file to the project classpath and import the needed classes from the com.shortnweb.sally package.
## Using the framework
### ShellVerbs
Each command available for use in the shell application is declared as a `ShellVerb`. The `ShellVerb` abstract class provides the basic structure for 
these. Each command you would like to include in the shell must be created as a new class extending `ShellVerb`. Below is an example of what a 
declaration should look like, followed by an explanation of everything shown.
```
import com.shortnweb.sally.ShellVerb;

public class VerbTest extends ShellVerb {
    
    // Values or Variables
    private boolean optSwitch;
    private String optText = "Default text value";
    private int optNum = 0;
    
    // Constructor
    public VerbTest() {
        // calling the super constructor, passing in the help documentation
        super("This verb is a test of the shell processor. Accepted options include text, num, and switch.");
        
        // adding each of the accepted options and linking them to setter methods
        this.addOption("switch", p -> setOptSwitch(!this.optSwitch))
                .addOption("text", this::setOptText)
                .addOption("num", p -> setOptNum(convertInt(p)))
                .addOption("s", p -> setOptSwitch(!this.optSwitch))
                .addOption("t", this::setOptText)
                .addOption("n", p -> setOptNum(convertInt(p)));
    }


    // get method (core functionality of the verb)
    @Override
    public String get() {
        return "Switch on? " + optSwitch + "\nText entered: " + optText +
                "\nNumber entered: " + optNum + " + 10 = " + (optNum+10);
    }
    
    // variable setter methods
    public void setOptSwitch(boolean optSwitch) {
        this.optSwitch = optSwitch;
    }

    public void setOptText(String optText) {
        this.optText = optText;
    }

    public void setOptNum(int optNum) {
        this.optNum = optNum;
    }
}

```
#### Values / Variables
The values included in the command / verb's class declaration each should correspond to a different configurable option for that command. 
These values at the very least need to have a setter method specified in order to change their value. It is recommended that a default value be 
specified for them at declaration.
#### Constructor
The abstract `ShellVerb` includes a basic constructor that accepts the help documentation for the command. Like with all inheriting classes, 
the call to the super constructor must come first. The next step is the most important and must be included in the constructor, building the optionMap.
#### optionMap
The `optionMap` is what is necessary to allow users of the application to change values or behavior of the command they are running. 
The `optionMap` must be put together within the constructor for its respective class (necessary for the `ShellProcessor` to call a command properly). 
The best way to build the `optionMap` is with the `addOption` method. This method accepts a string value as a key, and a method reference / 
lambda function as a value. 
Each specified key corresponds to what application users will need to type to reference a specific variable of the class. 
These will follow after either a -- or a - if using short notation (explained later on). If you wish to specify an option for use in short notation, 
simply make the key a single character (seen in the latter 3 entries). 
Each specified value should be a call to a setter method within the class. This can be entered as simple method reference for string values, 
as shown in the "text" and "t" entries, or a lambda function for values in need of conversion (such as numbers or booleans). 
The recommended lambda function for boolean values is demonstrated in the "switch" and "s" entries, passing the opposite of the boolean's current 
value into its setter method.
#### get() method
This is the most important method for all verbs, and is the only one required semantically (though setter methods for class variables are also 
needed for use in the `optionMap`). This method is where the behavior of the command is defined. This can refer to all the verb's local 
variables directly, no need to pass them in as parameters. 
#### Inherited static methods
There are 3 static methods included in the ShellVerb class: `convertInt`, `convertDouble`, and `call`. 
`convertInt` and `convertDouble` are included as a simple way of converting string input from the shell to a numeric value that can be used by the 
program. These make use of the `ObjectConverter.convert` method. That interface / method are included and can be used to convert any object to another, 
and may be useful for other string to object conversions when writing a shell application.
`call` is the method that is actually used to run a command when the user enters it into the shell. This method accepts a map of string keys and 
values and an instance of a `ShellVerb`. It will iterate through the map checking if the keys match any included in the verb's `optionMap`. 
When matches are found it will set the corresponding variables using the `optionMap` and will call the verb's `get()` method once finished.

### ShellProcessor
A `ShellProcessor` is the containing object that handles all input and output from the shell application and executes the relevant `ShellVerbs`.
The recommended use of this class is demonstrated below and explained afterwards. 
```
import com.shortnweb.sally.ShellProcessor;

public class ProcessTest {
    // declaring the main method that the application will run from
    public static void main(String[] args) {
        
        // creating a new ShellProcessor object, specifying the name of the shell and its help documentation
        ShellProcessor process = new ShellProcessor("test", "A test of the shell processor. Enter exit, help, or test");
        
        // adding the ShellVerbs that this processor accepts
        process.addVerb("test", VerbTest::new);
        
        // starting the application shell loop
        process.run();

    } // end of main function
} // end of ProcessTest class
```
#### Constructors
A `ShellProcessor` can be instantiated using 2 different constructors. One constructor simply requires a string parameter to list the help 
documentation for that shell application. The other constructor (seen above) accepts 2 parameters, the display name for the shell application,
and the help documentation. The name attribute is optional as it simply determines what (if anything) comes before the `#:` in the shell prompt.
#### verbMap and addVerb method
The `ShellProcessor` also contains a field known as `verbMap`. This is where all the application's accepted commands are stored. This map is structured
to have string keys that correspond to what command the user must type in the shell, mapped to supplier functions that construct a new instance of that
`ShellVerb` class. The most efficient way to populate that map is through the `addVerb` method, which is demonstrated in the example above.
As you can see, a string key is entered first, this is the exact string that a user must type in the shell prompt in order to execute that command.
The second parameter passed in is simply a method reference to a `ShellVerb`'s constructor method. This is because every time the shell runs a specified
command it must create a new instance of that method's containing class. The `addVerb` method returns a reference to the same object it was called
on, meaning that multiple method calls can be chained. Please note that 'help' and 'exit' are reserved verbs and cannot be overwritten via the 
`addVerb` or `setVerbMap` methods.
#### Running the processor
Once a `ShellProcessor` has been fully built, it can be run as a full application. As shown in the example above, simply invoke the object's `run`
method and everything else will be handled for you. Please note that once the `run` method is called the program enters into a loop. No other code after
the method call will be run until the end user enters 'exit' in the shell prompt and the loop closes.

### Interacting with the shell
Now that you have a working application, how do you use it? Below will be a few examples of console interactions to guide you in using and building
a Sally shell application.
#### Command structure
```
test#: test
Switch on? false
Text entered: null
Number entered: 0 + 10 = 10
test#: test -sn 20 --text words_no_spaces
Switch on? true
Text entered: words_no_spaces
Number entered: 20 + 10 = 30
test#: test -st "text with spaces and \"escaped quotes\"" -n 5
Switch on? true
Text entered: text with spaces and "escaped quotes"
Number entered: 5 + 10 = 15
```
This particular shell above is the result of the sample code shown off earlier, feel free to copy and run that code to play with it yourself.

First take note of the `test#:` at the beginning of lines 1, 5, and 9. This is the shell prompt, and the text at the beginning is what you can modify
using the `ShellProcessor` `name` value. We will focus on those lines listed, 1, 5, and 9 in order to break down the structure of a command.

The beginning of every command must be a verb. In the example above the verb `test` was used 3 times. The verb portion of the command corresponds to
a string key specified in the `verbMap` of the `ShellProcessor`. As seen on line 1, a verb can be entered without any options added, and its function
will still be carried out. If any options are to be specified, they will be listed afterwards separated by spaces, as with their values.

Looking at lines 5 and 9 you will see examples of options being used. Notice again the separation by spacing between verb, options, and values. The
first options specified on both lines use shorthand notation, beginning with a `-`. This allows multiple options to be specified at one time all in a 
group. The convention for shorthand notation requires that the beginning options specified in shorthand all be boolean values, with the last option 
having the potential to accept a value. This is demonstrated on both lines 5 and 9, the first example showing a numerical value being accepted, and the
second a string value. Please note that shorthand notation can be used for singular options as well, as demonstrated on line 9. If you desire any 
options to be used in shorthand notation, make sure you add them to the `optionMap` of the needed `ShellVerb` with a single character as a key.

Line 5 also demonstrates the long option notation on the second option specified. This is always started with `--` and requires that the full option
name be entered. This again corresponds to a string key added to an `optionMap`. Please note that when using long notation, a value will always be 
expected to follow the option name. This goes the same for boolean options. While shorthand notation allows a boolean to be switched without an entered
value, long notation requires something to follow before the next option can be entered.

Now please notice the string values entered after the `t` option on line 5, and after the `text` option on line 9. String values can be entered in 
two different ways. The first is shown on line 5, and is used when no spaces are desired within a string value. The second way is shown on line 9, 
when spaces are desired within a string it must be surrounded with quotes. Shown above are double quotes, but single quotes are also accepted. When 
using either double or single quotes to encase a string, you also can escape them within the value by using a backslash as demonstrated on line 9.
#### Help and Exit
```
test#: help
A test of the shell processor. Enter exit, help, or test
test#: help test
This verb is a test of the shell processor
test#: exit

Process finished with exit code 0
```
The `help` and `exit` commands are reserved by each shell for essential functions. If you look back at the code snippets from earlier in the tutorial, 
you will see where the `help` documentation was entered for both the `ShellProcessor` and the `ShellVerb` used in this example.

When using the `help` command, it can be called on its own or with a single option. As shown on lines 1 and 2, when used without options the `help`
command will print to the console whatever help documentation was entered for the `ShellProcessor`. This is designed to give some explanation about
your shell application and what commands it will accept. The single option that the `help` command will accept is always going to be the name of 
another command (verb). This must match the string that a user enters at the beginning of their command at the shell prompt. As shown in lines 3 and 4,
when this option is used the help documentation for that specific command or `ShellVerb` will be printed to the console. This is intended to explain
briefly what that command does, as well as what each of the options are that it uses.

The `exit` command is the intended way to close out of a shell application. This is simply entered on its own, and it will end the `run` method called
from the application's `ShellProcessor`. Once this is called code execution will resume after that method, running whatever is written afterward.