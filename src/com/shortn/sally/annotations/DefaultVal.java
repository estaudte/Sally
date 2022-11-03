package com.shortn.sally.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
public @interface DefaultVal {
    int integer();
    double doub();
    boolean bool();
    String str();
}
