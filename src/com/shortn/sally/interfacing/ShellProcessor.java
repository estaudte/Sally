package com.shortn.sally.interfacing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShellProcessor {

    private String name = "";
    private String help = "There is no help documentation available for this shell interface.";
    private Map<String, ShellVerb> verbMap;

    public ShellProcessor(String help) {
        this.help = help;
        this.verbMap = new HashMap<>();
    } // end of the simple constructor

    public ShellProcessor(String name, String help) {
        this.name = name;
        this.help = help;
        this.verbMap = new HashMap<>();
    } // end of  full constructor

    public String getName() {
        return name;
    } // end of getName method

    public void setName(String name) {
        this.name = name;
    } // end of setName method

    public String getHelp() {
        return help;
    } // end of getHelp method

    public void setHelp(String help) {
        this.help = help;
    } // end of setHelp method

    public Map<String, ShellVerb> getVerbMap() {
        return Collections.unmodifiableMap(verbMap);
    } // end of getVerbMap method

    public void setVerbMap(Map<String, ShellVerb> verbMap) {
        // making sure user cannot override the default help and exit commands
        if (verbMap.containsKey("help") || verbMap.containsKey("exit")) {
            throw new IllegalArgumentException("help and exit are reserved verbs!");
        } else {
            this.verbMap = verbMap;
        }
    } // end of setVerbMap method

    public ShellProcessor addVerb(String key, ShellVerb value) {
        // blocking users from adding help and exit and overriding their functions
        if (key.equalsIgnoreCase("help") || key.equalsIgnoreCase("exit")) {
            throw new IllegalArgumentException("help and exit are reserved verbs!");
        } else {
            this.verbMap.putIfAbsent(key, value);
        }
        // returning this object so that the method calls can be chained together
        return this;
    } // end of the addVerb method

    // primary method used to start up the shell interface
    public void run() {
        boolean isActive = true;
        while(isActive) {
            // displaying prompt on the screen to get user input
            System.console().printf(name + "#: ");
            // grabbing user input and storing it as a string array
            String[] input = System.console().readLine().split(" ");
            // starting conditional checking what the user entered
            if (input[0].equals("exit")) {
                // when the user enters the exit command, the shell processor loop terminates
                isActive = false;
            } else if (input[0].equals("help")) {
                // insert call to the help function (make this a separate method?)
            } else if (this.getVerbMap().containsKey(input[0])) {
                /* read this statement semi backwards:
                retrieve the verb entered by the user from the verbMap, add that as a parameter of the ShellVerb.call() method
                input as the other option, a String map linking option names to values, passing the user input into the asOptions method
                the call method will return a string
                The String output is immediately printed to the console */
                System.console().printf(ShellVerb.call(asOptions(input),this.getVerbMap().get(input[0])));
            } else {
                // if the entered verb is not found then it lets the user know and starts over
                System.console().printf(input[0] + " is not a known command.");
            } // end of the conditional checking the verb entered
        } // end of while loop to repeat the input / output process
    } // end of run method

    // when iterating through the in array, start at 1 instead of 0
    private Map<String, String> asOptions(String[] in) {

    } // end of asOptions method

} // end of ShellProcessor class
