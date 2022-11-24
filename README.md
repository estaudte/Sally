# Sally
Java framework to allow easy creation of shell-based applications and formatting of console output.
## Including in your project
You can download a zip archive including a JAR file of all needed resources, as well as the Javadoc for the framework. Add that JAR file to the project classpath and import the needed classes from the com.shortnweb.sally pacakge.
## Using the framework
### ShellVerbs
Each command available for use in the shell application is declared as a `ShellVerb`. The `ShellVerb` abstract class provides the basic structure for these. Each command you would like to include in the shell must be created as a new class extending `ShellVerb`. Below is an example of what a declaration should look like, followed by an explanation of everything shown.
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
The values included in the command / verb's class declaration each should correspond to a different configurable option for that command. These values at the very least need to have a setter method specified in order to change their value. It is recommended that a default value be specified for them at declaration.
#### Constructor
The abstract `ShellVerb` includes a basic constructor that accepts the help documentation for the command. Like with all inheriting classes, the call to the super constructor must come first. The next step is the most important and must be included in the constructor, building the optionMap.
#### optionMap
The `optionMap` is what is necessary to allow users of the application to change values or behavior of the command they are running. The `optionMap` must be put together within the constructor for it's respective class (necessary for the `ShellProcessor` to call a command properly). The best way to build the `optionMap` is with the `addOption` method. This method accepts a string value as a key, and a method reference / lambda function as a value. 
Each specified key corresponds to what application users will need to type to reference a specific variable of the class. These will follow after either a -- or a - if using short notation (explained later on). If you wish to specify an option for use in short notation, simply make the key a single character (seen in the latter 3 entries). 
Each specified value should be a call to a setter method within the class. This can be entered as simple method reference for string values, as shown in the "text" and "t" entries, or a lambda function for values in need of conversion (such as numbers or booleans). The recommended lambda function for boolean values is demonstrated in the "switch" and "s" entries, passing the opposite of the boolean's current value into its setter method.
#### get() method
This is the most important method for all verbs, and is the only one required semantically (though setter methods for class variables are also needed for use in the `optionMap`). This method is where the behavior of the command is defined. This can refer to all of the verb's local variables directly, no need to pass them in as parameters. 
#### Inherited static methods
There are 3 static methods included in the ShellVerb class: `convertInt`, `convertDouble`, and `call`. 
`convertInt` and `convertDouble` are included as a simple way of converting string input from the shell to a numeric value that can be used by the program. These make use of the `ObjectConverter.convert` method. That interface / method are included and can be used to convert any object to another, and may be useful for other string to object conversions when writing a shell application.
`call` is the method that is actually used to run a command when the user enters it into the shell. This method accepts a map of string keys and values and an instance of a `ShellVerb`. It will iterate through the map checking if the keys match any included in the verb's `optionMap`. When matches are found it will set the corresponding variables using the `optionMap` and will call the verb's `get()` method once finished.
