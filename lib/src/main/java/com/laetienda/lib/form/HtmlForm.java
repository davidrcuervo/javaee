package com.laetienda.lib.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HtmlForm {
	
	String name();
	FormMethod method() default FormMethod.POST;
	String action() default "";
	String button() default "";
}
