/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this annotation to add default response JSON class, and to add
 * possible response codes
 * 
 * @see RestCode
 * @author os
 */
@Target(value = { ElementType.TYPE, ElementType.METHOD })
public @interface RestReturn {
    /**
     * For class Annotation, this is the default JSON response class.
     * For method Annotation, this is the specific JSON response class.
     * 
     * @return
     */
    public Class value() default Object.class;

    /**
     * If response value is a list, this is the inner JSON response class
     * 
     * @return the (inner) JSON response class
     */
    public Class entity() default Object.class;


    /**
     * List possible RestCodes
     * 
     * @return possible RestCodes
     */
    public RestCodes[] codes() default { };

    /**
     * @return true if the class-scope parameters are declared by this dummy method.
     */
    public boolean declaresClassParams() default false;

    /**
     * set to true to include documentation for class-scope parameters.
     * 
     * @return true if class-scope parameters should be documented
     * @see RestReturn#classParams()
     */
    public boolean supportsClassParams() default false;

    /**
     * Set to true to indicate that this method is secured by what even security framework this service is using.
     * 
     * @return true if the methods is protected by authentication
     */
    public boolean isSecured() default false;
    
    /**
     * set to message string to display this message in rest-doclet API part when you have the same rest-api path
     * @return message string if set
     * */
    public String highlightApiMessage() default "";
}
