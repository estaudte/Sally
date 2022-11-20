package com.shortn.sally.interfacing;

import java.util.*;
import java.util.function.Supplier;

/** Handles the input and output necessary to operate a complete shell application.
 * <p>This class handles all input processing and logic needed for user interaction with a list of {@link ShellVerb ShellVerbs}.
 * The primary value needed in any use of this class is the {@link #verbMap verbMap} which stores a list of defined verb constructors
 * and links them to the string value the user must enter to reference them. All output and input will be handled by {@link System#in System.in}
 * and {@link System#out System.out}, the location these print and read from can be overwritten in the Main class.</p>
 * <p>The recommended use of this class is to create a new instance assigned to a variable, then utilize that variable to map
 * the needed <code>Strings</code> to the accepted <code>ShellVerbs</code>, and finally call the {@link #run() run} method.
 * The program will loop within the <code>run</code> method until the user enters in an <code>exit</code> command.</p>
 * <p>When adding accepted verbs to the processor, note that "help" and "exit" are not allowed to be used as they are reserved for the processor.
 * The built-in "help" verb will display the {@link #help help} field assigned to the processor, or to whatever verb is typed after it,
 * separated by a space. The built-in "exit" verb is entered when users want to close out of the shell interface. Once this is entered the
 * processors <code>run</code> method will stop executing, and the application will move on to whatever code comes after that call.</p>
 * <p>The shell interface looks very similar to any standard Unix terminal. The user will be greeted by this prompt: <code>#:</code>
 * That prompt can be modified by specifying a {@link #name name} value for the processor, but this is not required. Here is an example of
 * how a typical command will look as entered into the shell interface:</p>
 * <p><code>#: verb -bv 10 --text "Here is some text with spaces" -t text_no_spaces</code></p>
 * <p>Notice the structure of the above command. First is entered the specified call value for the desired verb (the <code>key</code>
 * specified in the <code>verbMap</code>). Each argument afterward is separated by a space. There are two ways of specifying options when calling
 * a verb. The first is with a single -. This short notation only needs a single character to locate the desired option, and one or multiple options can
 * be specified at once. When multiple options are used, it is best to keep those at the beginning to only <code>boolean</code> options, and
 * if desired end the statement with a single option accepting a value.</p><p>Alternatively, you can specify options with a double --. This
 * long notation will search for an exact match to one option linked to that entire string. When entering a <code>String</code> as text, there are
 * several options. For entries without spaces in them, you can simply type in the full value and it will be accepted. If you need to add spaces,
 * you can surround the value in either double " or single ' quotes. If you need to enter one of those symbols in part of the value, escape
 * the character by placing a \ in front of it.</p>
 * @author Ethan Staudte
 * @version 1.0.0*/
public class ShellProcessor implements Runnable {

    /** Used to name this processor when prompting the user. Value will be displayed before the "#: ".
     * Defaults to an empty string.
     * @see #getName()
     * @see #setName(String) */
    private String name = "";
    /** Stores documentation about the processor, anything considered relevant to assist with use.
     * Recommended to include a list of accepted verbs.
     * Defaults to a statement that no help documentation is available.
     * @see #getHelp() 
     * @see #setHelp(String) 
     * @see #help(String[]) */
    private String help = "There is no help documentation available for this shell interface.";
    /** Stores a list of verb names (<code>String keys</code>), to the associated verb instance (<code>ShellVerb constructors</code>).
     * The specified keys are what the processor checks user input against for valid commands.
     * Each time a match is found, a new instance of the verb is created and passed into the {@link ShellVerb#call(Map, ShellVerb) call()} method.
     * @see #getVerbMap() 
     * @see #setVerbMap(Map) 
     * @see #addVerb(String, Supplier) */
    private Map<String, Supplier<? extends ShellVerb>> verbMap = new HashMap<>();

    /** Constructs a new <code>ShellProcessor</code> with the specified <code>help</code> documentation and an empty <code>verbMap</code>.
     * @param help <code>String</code> formatted to document what the processor and its verbs do.*/
    public ShellProcessor(String help) {
        this.help = help;
    } // end of the simple constructor

    /** Constructs a new <code>ShellProcessor</code> with the specified <code>name</code>, <code>help</code> documentation,
     * and an empty <code>verbMap</code>.
     * @param name <code>String</code> to be displayed before the "#: " in the shell prompt.
     * @param help <code>String</code> formatted to document what the processor and its verbs do.*/
    public ShellProcessor(String name, String help) {
        this.name = name;
        this.help = help;
    } // end of  full constructor

    /** Returns the "name" of the <code>ShellProcessor</code> application.
     * @return <code>String</code> value stored in the {@link #name name} field. */
    public String getName() {
        return name;
    } // end of getName method

    /** Sets a new value for the "name" of the <code>ShellProcessor</code> application.
     * @param name <code>String</code> value of the desired new {@link #name name}.*/
    public void setName(String name) {
        this.name = name;
    } // end of setName method

    /** Returns the help documentation of the <code>ShellProcessor</code> application.
     * @return <code>String</code> value stored in the {@link #help help} field. */
    public String getHelp() {
        return help;
    } // end of getHelp method

    /** Sets the documentation for the <code>ShellProcessor</code> application.
     * Recommended to include information on what verbs are accepted and what they do.
     * @param help <code>String</code> value to be stored in the {@link #help help} field. */
    public void setHelp(String help) {
        this.help = help;
    } // end of setHelp method

    /** Returns an immutable copy of the processor's {@link #verbMap verbMap} field.
     * @return {@link Collections#unmodifiableMap(Map) unmodifiableMap} copied from the <code>verbMap</code>.*/
    public Map<String, Supplier<? extends ShellVerb>> getVerbMap() {
        return Collections.unmodifiableMap(verbMap);
    } // end of getVerbMap method

    /** Sets a new value for the {@link #verbMap verbMap} field.
     * @param verbMap {@link Map Map} linking <code>String</code> keys to {@link Supplier Supplier} values. <code>Supplier</code> functions must
     * return an object that extends the {@link ShellVerb ShellVerb} class.*/
    public void setVerbMap(Map<String, Supplier<? extends ShellVerb>> verbMap) {
        // making sure user cannot override the default help and exit commands
        if (verbMap.containsKey("help") || verbMap.containsKey("exit")) {
            throw new IllegalArgumentException("help and exit are reserved verbs!");
        } else {
            this.verbMap = verbMap;
        }
    } // end of setVerbMap method

    /** Adds a new key - value pairing to the {@link #verbMap verbMap}. Note that the terms "help" and "exit" are reserved by the processor.
     * @param key <code>String</code> value that the user will enter as a command.
     * @param value {@link Supplier Supplier} method that returns a new instance of a {@link ShellVerb ShellVerb}.
     * @return <code>ShellProcessor</code> reference to <code>this</code> object, used to chain calls to this method.*/
    public ShellProcessor addVerb(String key, Supplier<? extends ShellVerb> value) {
        // blocking users from adding help and exit and overriding their functions
        if (key.equalsIgnoreCase("help") || key.equalsIgnoreCase("exit")) {
            throw new IllegalArgumentException("help and exit are reserved verbs!");
        } else {
            this.verbMap.putIfAbsent(key, value);
        }
        // returning this object so that the method calls can be chained together
        return this;
    } // end of the addVerb method

    /** Starts the shell application running and handles all user input.
     * <p>This method will loop indefinitely until the user enters in the "exit" command.</p>
     * <p>This method will print and read input using {@link System#in System.in} and {@link System#out System.out}</p> */
    public void run() {
        boolean isActive = true;
        Scanner sc = new Scanner(System.in);
        while(isActive) {
            // grabbing user input (with prompt) and storing it as a string array, separating entries by spaces
            System.out.print(name + "#: ");
            String[] input = sc.nextLine().split(" ");
            // starting conditional checking what the user entered
            if (input[0].equals("exit")) {
                // when the user enters the exit command, the shell processor loop terminates
                isActive = false;
            } else if (input[0].equals("help")) {
                // call the help function when the user enters that command
                System.out.print(this.help(input) + "\n");
            } else if (this.getVerbMap().containsKey(input[0])) {
                /* The syntax below is not the clearest so here is a brief run-down.
                * The conditional statement above checks to see if the first string entered by the user is one of the verbMap's accepted commands.
                * If it is an accepted command, we then create a new instance of that verb, using the constructor method value from the map.
                * A new instance is created so that every time the method is called the values are reset to default unless overridden. Also prevents
                * from overloading memory.
                * We then check if the user entered any options after the initial command.
                * If they did enter in more options, we  then start the super long method call.
                * We encapsulate the entire thing within the stdOut.get, print command to immediately print the method's string output to the user.
                * The method we call is the ShellVerb.call method to process a list of options on a specified verb.
                * In this case, the options we pass in start as an array that must then be converted into a String, String map.
                * We call the asOptions method to convert the array into that map.
                * Before we pass the array in, however, we need to clean up the array.
                * We only want the remaining arguments after the command to be processed, so we perform a copyOfRange on the values to get what we need.
                * This copied array is then passed through the linkStrings method to make sure any string entries surrounded by quotes are reconstructed.
                * That final product of a processed array is then what gets passed into the asOptions method, and it used along with the newly created
                * verbInstance variable in the call method.
                * If no additional arguments are specified by the user, we simply pass in a blank map of options, and the same verbInstance to run
                * the verb with its default options.*/
                ShellVerb verbInstance = this.getVerbMap().get(input[0]).get();
                if (input.length > 1) {
                    // only truncate the user input array if there are any options
                    System.out.print(ShellVerb.call(asOptions(linkStrings(Arrays.copyOfRange(input, 1, input.length))), verbInstance) + "\n");
                } else {
                    // otherwise skip the crazy processing and just run the command
                    System.out.print(ShellVerb.call(new HashMap<>() {}, verbInstance) + "\n");
                } // end of conditional checking if the input had any options or just a verb
            } else {
                // if the entered verb is not found then it lets the user know and starts over
                System.out.print(input[0] + " is not a known command.\n");
            } // end of the conditional checking the verb entered
        } // end of while loop to repeat the input / output process
    } // end of run method

    private Map<String, String> asOptions(String[] in) {
        // creating a new temporary map to update from the array contents
        Map<String, String> res = new HashMap<>();
        for (int i = 0; i < in.length; i++) {
            // checking if the option is specified to use long notation
            if (in[i].startsWith("--")) {
                // next checking if there is a next value
                if (i < in.length -1) {
                    // checking if that next value is an option
                    if (!in[i+1].startsWith("--") && !in[i+1].startsWith("-")) {
                        // if it is not an option then add it as a value
                        // using reassignment operator as second index so that the loop automatically knows to search from the next index
                        res.put(in[i].substring(2), in[i+=1]);
                    } // end of conditional checking if next value is an option
                } else {
                    // if it is an option then simply specify this as a boolean
                    res.putIfAbsent(in[i].substring(2), null);
                } // end of conditional checking if the next string is an option
                // since the check for a -- already failed, we know that if the option starts with a - it has to be an abbreviation
            } else if (in[i].startsWith("-")) {
                // retrieving the length of the shorthand option specification
                String multOp = in[i].substring(1);
                // if there is more than one option specified, process accordingly
                if (multOp.length() > 1) {
                    for (int j = 0; j < multOp.length() - 1; j++) {
                        // iterating through the multiple abbreviations aside from last one and entering just the abbreviation / a blank value for boolean
                        res.putIfAbsent(String.valueOf(multOp.charAt(j)), null);
                    } // end of loop through the first majority of options
                } // end of conditional for multiple option processing
                // regardless of if the one option or multiple options are specified in shorthand, process the last option this way:
                // check if there is a next value, if not, or if the next value isn't an option, add as boolean
                String nextKey = String.valueOf(multOp.charAt(multOp.length()-1));
                if (i < in.length-1) {
                    // checking if the next value is an option
                    if (!in[i+1].startsWith("--") && !in[i+1].startsWith("-")) {
                        // using the reassignment when adding the next value here to reduce line count
                        res.putIfAbsent(nextKey, in[i+=1]);
                    } else {
                        res.putIfAbsent(nextKey, null);
                    }// end of check if the next value is an option
                } else {
                    res.putIfAbsent(nextKey, null);
                } // end of check if there is a next value
            } // end of conditional checking for option tags (either -- or -)
        } // end of for loop creating the map
        // returning the final hashmap, making sure it can't have any data leaks
        return Collections.unmodifiableMap(res);
    } // end of asOptions method

    private String help(String[] in) {
        // checking if the user specified a verb to get help on or not
        if (in.length > 1) {
            // checking if the specified verb exists
            if (this.verbMap.containsKey(in[1])) {
                // if it does exist, return its help value
                ShellVerb spec = this.verbMap.get(in[1]).get();
                return spec.getHelp();
            } else {
                return in[1] + " not recognized.";
            } // end of check if user specified a valid verb to retrieve help
        } else {
            // if no verb is specified the help info for the shell processor is displayed
            return this.help;
        } // end of check for additional options
    } // end of help function

    // this is what is used to connect strings that are surrounded by quotes and produce a new array
    private String[] linkStrings(String[] in) {
        // creating a new array to store temporary values in
        String[] processed = new String[in.length];
        for (int i = 0; i < in.length; i++) {
            // the string entered will either just be the exact one from the old array, or a base for a longer input
            StringBuilder temp = new StringBuilder(in[i]);
            // this makes sure the new array is not skipping places when adding values
            int n = i;
            // testing for a string start character and then searching through to find the rest of the string value
            if (in[i].startsWith("\"")) {
                // re-entering the current string without the beginning quote, and adding back the space that was removed
                temp = new StringBuilder(in[i].substring(1)).append(' ');
                // iterating through the remaining strings checking for the end of this joined value
                // using reassignment operator here because it saves extra writing to make sure i stays up to date
                for (int j = i+=1; j < in.length; j++) {
                    // if the string ends with only a quote (not an escaped quote as that should be included in the final value) it finishes building
                    if (in[j].endsWith("\"") && !in[j].endsWith("\\\"")) {
                        // adding the ending portion of the string without the quote
                        temp.append(in[j], 0, in[j].length() - 1);
                        // quitting the loop so that it no longer searches / builds the string
                        break;
                    } else {
                        // adding in the next located value and the space that was removed at input
                        temp.append(in[j]).append(' ');
                    } // end of conditional checking for the string end
                } // end of loop searching for the end of the string / building
                // now performing the exact same check / operation just for a different delimiter (notes on function listed above)
            } else if (in[i].startsWith("'")) {
                temp = new StringBuilder(in[i].substring(1)).append(' ');
                for (int j = i+=1; j < in.length; j++) {
                    if (in[j].endsWith("'") && !in[j].endsWith("\\'")) {
                        temp.append(in[j], 0, in[j].length() - 1);
                        break;
                    } else {
                        temp.append(in[j]).append(' ');
                    }
                } // end of loop searching for the end of the string / building
            } // end of conditional looking for start of string
            processed[n] = temp.toString();
        } // end of for loop processing / connecting strings
        // truncating the array to get rid of the empty spaces at the end before returning it (filter method)
        // at this point also filtering through the final stream to remove the escape characters (map method)
        return Arrays.stream(processed).filter(e -> (e != null && !e.isEmpty())).map((e ->
                e.replace("\\\"", "\"").replace("\\'", "'"))).toArray(String[]::new);
    } // end of the linkStrings method
} // end of ShellProcessor class
