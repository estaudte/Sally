package com.shortn.sally.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface ShellVerb {
    String name();
    String help();
}
