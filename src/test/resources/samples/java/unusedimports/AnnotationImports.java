package samples;

import java.lang.annotation.Retention;        // USED
import java.lang.annotation.RetentionPolicy;  // USED

import java.lang.annotation.Target;           // USED
import java.lang.annotation.ElementType;      // USED

import java.lang.annotation.Documented;       // UNUSED

public class AnnotationImports {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MyAnnotation {
    }
}
