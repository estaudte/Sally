package com.shortn.sally.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.ElementType;
import java.util.Set;

@SupportedAnnotationTypes("com.shortn.sally.annotations.*")
public class ProcessorBuilder extends AbstractProcessor {

    public ProcessorBuilder() {
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // first layer is retrieving the elements said to be a ShellProcessor, using this context to build a file
        for (Element sp : roundEnv.getElementsAnnotatedWith(ShellProcessor.class)) {
            // looking through the specified source file and build each of the methods into its own class
            // here we save the source location to a variable so it can be referenced easily
            Class<?> src = sp.getAnnotation(ShellProcessor.class).verbSource();
            // iterating through the ShellVerb annotations (method declarations)
            for (Element sv : roundEnv.getElementsAnnotatedWith(ShellVerb.class)) {
                // checking if the ShellVerb is within the correct source file for this processor
                if (sv.getEnclosingElement().getClass() == src) {
                    // if true, begin writing the method it into its own class
                    // getting the name of the method to reuse when writing the class / adding to the VerbMap
                    String name = sv.getSimpleName().toString();
                    // figure out how to retrieve a map or other collection of parameters (names and types) to make fields
                    // retrieve the abbreviations and defaults (if either of any are specified) and link in a map to the parameter name
                    // once class is written, write a statement adding it to the VerbMap of the ShellProcessor

                } // end of check to see if the method is part of the specified source file
            } // end of for loop through the ShellVerb annotations
        } // end of loop through the ShellProcessor annotations
        // returning true so the options are not passed to any other processors
        return true;
    }
}
