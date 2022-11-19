package com.shortn.sally.interfacing;

import com.shortn.sally.ObjectConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides the primary operation and structure for any shell commands (referred to as verbs).
 * <p>The values inherited from this class include a <code>String</code> to store the help documentation,
 * and a <code>Map</code> to store the options accepted by the verb.
 * The {@link #optionMap map of options} links a string (what is typed after a - in the command) to a setter method for one of the verbs values.
 * The link to the setter method is specified via a lambda function. This class also includes
 * protected methods to easily pass string values into the needed primitive types for the map,
 * all implementers of the {@link ObjectConverter ObjectConverter} interface.</p>
 * <p>Every extending class defined by a user should specify the needed options as primitive values of the class,
 * along with the appropriate getter and setter methods.</p>
 * <p>Each verb should be triggered by the {@link #call call} method.
 * This accepts a map of string option names to the appropriate
 * string value specified by the user and converts them via the <code>optionMap</code> methods.
 * By referencing those methods kept in the <code>optionMap</code> the <code>call</code> method gets the class values in the proper state
 * before calling the user specified {@link #get get} method.
 * The <code>get</code> method specified by the user should simply directly reference / use the class values they created,
 * and return a <code>String</code> value to be printed to the console upon completion of the action.
 * The get method should only be used when referenced within the <code>call</code> method.</p>
 * @author Ethan Staudte
 * @version 1.0.0*/
public abstract class ShellVerb implements Supplier<String> {

    // fields
    /** Stores documentation about what the verb does.
     * Recommended to include a list of accepted options / abbreviations.
     * Defaults to a warning stating that the verb has no help documentation.
     * @see #getHelp() 
     * @see #setHelp(String) */
    private String help = "This action does not include any help documentation.";
    /** Stores a list of option names (<code>String</code> keys), linked to the setter method used to change it ({@link Consumer Consumer} values).
     * Users will reference these using either a double -- or a single - before the specified option name. If short (single -) notation is desired
     * for an option, make sure to add a mapping of a single letter to the appropriate setter method. Otherwise, double -- notation will be required,
     * along with typing the full option name.
     * @see #getOptionMap() 
     * @see #setOptionMap(Map)
     * @see #addOption(String, Consumer) */
    private Map<String, Consumer<String>> optionMap;

    /** Constructs a new <code>ShellVerb</code> with the specified {@link #help help} documentation, and an empty {@link #optionMap optionMap}.
     * <p>Inheriting classes should send their desired help docs via the <code>super</code> constructor,
     * and populate the <code>optionMap</code> using the {@link #addOption(String, Consumer) addOption} method.</p>
     * @param help <code>String</code> formatted to document what the verb and its options do.
     * @see #addOption(String, Consumer) */
    public ShellVerb(String help) {
        this.help = help;
        this.optionMap = new HashMap<>();
    } // end of constructor

    // getters & setters
    /** Returns the formatted <code>String</code> documenting what the verb and its options do.
     * @return <code>String</code> value stored in the {@link #help help}  field. */
    public String getHelp() {return this.help;}
    /** Specifies a new value for the {@link #help help} field to document the verb and option usage.
     * @param help <code>String</code> formatted to document what the verb and its options do. */
    public void setHelp(String help) {this.help = help;}
    /** Returns an immutable copy of the {@link #optionMap optionMap} field to iterate through and run methods from.
     * @return {@link java.util.Collections#unmodifiableMap Map}
     * immutable copy of the <code>optionMap</code> variable. */
    public Map<String, Consumer<String>> getOptionMap() {return Collections.unmodifiableMap(this.optionMap);}
    /** Specifies a new value for the {@link #optionMap optionMap} field to link string input to class setter methods.
     * <p>The <code>String</code> key should match what the shell user will enter to specify a verb option.
     * The {@link Consumer Consumer} value should be specified as a lambda function converting a <code>String</code> value into the
     * appropriate setter method for one of this class' variables.</p>
     * @param options {@link Map Map}
     * a preset Map linking string values to this class' setter methods
     * @see #convertInt(String)
     * @see #convertDouble(String) */
    public void setOptionMap(Map<String, Consumer<String>> options) {this.optionMap = options;}
    /** Adds a new mapping of a <code>String</code> key to a {@link Consumer Consumer} setter method for this class' variables.
     * Method calls can be chained as it returns a reference to the current object once run.
     * @param key <code>String</code> that will be entered by a user to specify a verb option.
     * @param value <code>Consumer</code> method, specified as a lambda function to pass a converted <code>String</code> value into the
     * appropriate setter method for one of this class's variables.
     * @return <code>ShellVerb</code> reference to the current object. Allows multiple method calls to be chained.
     * @see #convertInt(String)
     * @see #convertDouble(String) */
    public ShellVerb addOption(String key, Consumer<String> value) {
        this.optionMap.putIfAbsent(key, value);
        return this;
    }

    /** Used to both set this class' local variables and run the user specified {@link #get() get} function based on them.
     * When entering a value for the <code>verb</code> parameter you should use a <code>new</code> declaration of a specific
     * <code>ShellVerb</code> subclass.
     * @param <T> Any class inheriting from the <code>ShellVerb</code> class. This will be a user-defined
     *           verb with its own local variables and defined <code>get</code> method to perform whatever action is needed.
     * @param values {@link Map Map} linking the option name <code>key</code> to the user specified <code>value</code>.
     * @param verb New instance of {@link T T} to be newly instantiated with each method call
     *                        and have its <code>optionMap</code> checked against and executed from.
     * @return <code>String</code> to be returned to the console stating the execution status or success. */
    public static final <T extends ShellVerb> String call(Map<String, String> values, T verb, Consumer<String> stdOut) {
        // first creating a variable within the method scope to easily reference the immutable optionMap of the verb being called
        Map<String, Consumer<String>> opt = verb.getOptionMap();
        /* iterating through the map of user provided strings, 1st string is the key, should match a key in the optionMap
        * 2nd string is the value passed into the method used */
        values.forEach((s, m) -> {
            // checking if the key matches any provided in the map, if so, call its setter method and pass the needed value
            if (opt.containsKey(s)) {
                opt.get(s).accept(m);
            // if the option is invalid, let the user know it couldn't be found
            } else {
                stdOut.accept("ERROR: No " + s + " option found!\n");
            }
        });
        // MUST BE THE LAST LINE OF METHOD
        // once the verbs values have been set appropriately, call the user specified action
        return verb.get();
    } // end of call method

    /** Used to convert a String value entered at the command line into an integer value for use by the get() method.
     * Recommended use is in the optionMap as part of a lambda function to set a new local value for this class.
     * @param s <code>String</code> value entered by the user in the command line to be converted.
     * @return <code>int</code> value derived from the entered string.
     * @see ObjectConverter
     * @see ObjectConverter#convert(Object, Function) */
    public static final int convertInt(String s) {
        return ObjectConverter.convert(s, Integer::valueOf);
    } // end of convertInt method

    /** Used to convert a String value entered at the command line into a double value for use by the get() method.
     * Recommended use is in the optionMap as part of a lambda function to set a new local value for this class.
     * @param s <code>String</code> value entered by the user in the command line to be converted.
     * @return <code>double</code> value derived from the entered string.
     * @see ObjectConverter
     * @see ObjectConverter#convert(Object, Function) */
    public static final double convertDouble(String s) {
        return ObjectConverter.convert(s, Double::valueOf);
    } // end of convertDouble method

} // end of ShellVerb class
