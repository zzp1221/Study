package com.project.test.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicRequireAuthority {
    
    String[] value();
    
    Logical logical() default Logical.OR;
    
    enum Logical {
        AND,
        OR
    }
}
