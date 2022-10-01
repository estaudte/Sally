package com.shortn.sally;

import java.util.function.Function;

// a simple interface that allows you to create a processor to easily convert one object type to another
// this method is used by the optionMap to more easily convert a string value into a different object
public interface ObjectConverter {
        static <I,O> O convert(I in, Function<I, O> parser) {
            return parser.apply(in);
        } // end of process method
} // end of ObjectConverter interface
