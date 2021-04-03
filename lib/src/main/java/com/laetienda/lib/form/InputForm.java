package com.laetienda.lib.form;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface InputForm {
	
	String name();
	String label();
	String id(); 		//If default use the name of the variable
	InputType type() default InputType.TEXT;
	int order() default 0;
	String placeholder();
	String options() default "";
}
