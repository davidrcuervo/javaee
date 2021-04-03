package com.laetienda.model.lib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ValidateParameters {
	
	String name();
	boolean nullable() default true;
	int minlenght() default 0;
	int maxlenght() default 254;
	int minvalue() default 0;
	String regex() default "";
	String message() default "";
}
