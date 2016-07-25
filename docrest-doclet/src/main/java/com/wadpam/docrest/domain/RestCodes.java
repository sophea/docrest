/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this annotation for each possible response code
 * 
 * @author rady
 */
@Target(value = { ElementType.METHOD })
public @interface RestCodes {
    /**
     * The HTTP response code
     * @return 
     */
    public String codes();
}
