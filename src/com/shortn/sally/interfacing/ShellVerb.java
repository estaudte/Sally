package com.shortn.sally.interfacing;

import com.shortn.sally.ObjectConverter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Provides the primary operation and structure for any shell commands (referred to as verbs).
 * <p>The values inherited from this class include a <code>String</code> to store the help documentation,
 * and a <code>Map</code> to store the options accepted by the verb.
 * The map of options links a string (what is typed after a - in the command) to a setter method for one of the verbs values.
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
     * Defaults to a warning stating that the verb has no help documentation.
     * @see #getHelp() 
     * @see #setHelp(String) */
    private String help = "This action does not include any help documentation.";
    /** Stores a list of option names (<code>String</code> keys), linked to the setter method used to change it ({@link Consumer Consumer} values).
     * @see #getOptionMap() 
     * @see #setOptionMap(Map)
     * @see #addOption(String, Consumer) */
    private Map<String, Consumer<String>> optionMap;

    /** Constructs a new ShellVerb with the specified <code>help</code> documentation, and an empty <code>optionMap</code>.
     * <p>Inheriting classes should send their desired help docs via the <code>super</code> constructor,
     * and populate the <code>optionMap</code> using the {@link #addOption(String, Consumer) addOption} method.</p>
     * @param help <code>String</code> formatted to document what the verb and its options do.
     * @see #addOption(String, Consumer) */
    public ShellVerb(String help) {
        this.help = help;
        this.optionMap = new HashMap<String, Consumer<String>>();
    } // end of constructor

    // getters & setters
    /** Returns the formatted <code>String</code> documenting what the verb and its options do.
     * @return <code>String</code> value stored in the <code>help</code> variable. */
    public String getHelp() {return this.help;}
    /** Specifies a new value for the <code>help</code> variable to document the verb and option usage.
     * @param help <code>String</code> formatted to document what the verb and its options do. */
    public void setHelp(String help) {this.help = help;}
    /** Returns an immutable copy of the <code>optionMap</code> variable to iterate through and run methods from.
     * @return {@link java.util.Collections#unmodifiableMap Map<String, Consumer>}
     * immutable copy of the <code>optionMap</code> variable. */
    public Map<String, Consumer<String>> getOptionMap() {return Collections.unmodifiableMap(this.optionMap);}
    /** Specifies a new value for the <code>optionMap</code> variable to link string input to class setter methods.
     * <p>The <code>String</code> key should match what the shell user will enter to specify a verb option.
     * The <code>Consumer</code> value should be specified as a lambda function converting a <code>String</code> value into the
     * appropriate setter method for one of this class' variables.</p>
     * @param options {@link Map Map<String, Consumer>}
     * a preset Map linking string values to this class' setter methods
     * @see #processBoolean(String)
     * @see #processInt(String)
     * @see #processDouble(String) */
    public void setOptionMap(Map<String, Consumer<String>> options) {this.optionMap = options;}
    /** Adds a new mapping of a <code>String</code> key to a {@link Consumer Consumer<String>} setter method for this class' variables.
     * @param key <code>String</code> that will be entered by a user to specify a verb option.
     * @param value <code>Consumer</code> method, specified as a lambda function to pass a converted <code>String</code> value into the
     * appropriate setter method for one of this class's variables.
     * @see #processBoolean(String)
     * @see #processInt(String)
     * @see #processDouble(String) */
    public void addOption(String key, Consumer<String> value) {
        this.optionMap.putIfAbsent(key, value);
    }

    /** Used to both set this class' local variables and run the user specified {@link #get() get} function based on them.
     * This method should be what is referenced via lambda function in the map for the ShellProcessor.
     * When entering a value for the <code>verb</code> parameter you should use a <code>new</code> declaration of a specific
     * <code>ShellVerb</code> subclass.
     * @param values {@link Map Map<String, String} linking the option name <code>key</code> to the user specified <code>value</code>.
     * @param verb {@link T T} any subclass extending from <code>ShellVerb</code> to be newly instantiated with each method call
     *                        and have its <code>optionMap</code> checked against and executed from.
     * @return <code>String</code> to be returned to the console stating the execution status or success. */
    public static final <T extends ShellVerb> String call(Map<String, String> values, T verb) {
        // code to iterate through the value map and set the needed values before running the verb function (last line)
        Map<String, Consumer<String>> opt = verb.getOptionMap();
        values.forEach((s, m) -> {
            if (opt.containsKey(s)) {
                opt.get(s).accept(m);
            }
        });
        // last line of method
        return verb.get();
    } // end of run method

    // easier implementation to directly convert Strings to booleans for optionMap
    protected static final boolean processBoolean(String s) {
        return ObjectConverter.convert(s, Boolean::valueOf);
    } // end of processBoolean method

    // easier implementation to directly convert Strings to ints for optionMap
    protected static final int processInt(String s) {
        return ObjectConverter.convert(s, Integer::valueOf);
    } // end of processInt method

    // easier implementation to directly convert Strings into doubles for optionMap
    protected static final double processDouble(String s) {
        return ObjectConverter.convert(s, Double::valueOf);
    } // end of processDouble method

} // end of ShellVerb class
