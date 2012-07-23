package org.geotools.feature.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XSDMapping {
	String local() default "";
	String namespace() default "";
	String separator() default "";
	String path() default "";	
}
