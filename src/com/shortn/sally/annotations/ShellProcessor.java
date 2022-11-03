package com.shortn.sally.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
public @interface ShellProcessor {
    // this allows the user to create a generic class file that holds all annotated verb methods
    Class<?> verbSource();
}
