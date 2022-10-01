package com.shortn.sally;

import java.util.function.Function;

// a simple interface that allows you to create a processor to easily convert one object type to another
// this method is used by the optionMap to more easily convert a string value into a different object
/** Allows the creation of a processor method that will easily convert an object of one type into another.
 * <p>This interface's only method <code>convert</code> is used by the {@link com.shortn.sally.interfacing.ShellVerb ShellVerb} class
 * for 2 internal methods, <code>convertInt</code> and <code>convertDouble</code></p>
 * @author Ethan Staudte
 * @version 1.0.0
 * @see com.shortn.sally.interfacing.ShellVerb*/
public interface ObjectConverter {
    /** Converts any specified object into a new object of a different specified type, via a defined conversion function.
     * This method automatically enters the <code>in</code> parameter to the <code>action</code> parameter's {@link Function#apply apply}
     * method and returns whatever output is generated.
     * @param <I> Accepts any object, defines the type to be accepted as input.
     * @param <O> Accepts any object, defines the type to be returned as output.
     * @param in {@link I I} object to be input to the function and converted.
     * @param action Lambda expression (or method call) implementing the {@link Function Function} interface. Must accept
     *              an object the same type as <code>I</code> and process it to return an object of type <code>O</code>.
     * @return {@link O O} object produced as a result of the conversion function. */
    static <I,O> O convert(I in, Function<I, O> action) {
            return action.apply(in);
        } // end of convert method
} // end of ObjectConverter interface
