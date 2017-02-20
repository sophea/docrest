package com.wadpam.docrest.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <p>
 * AuthorizationTest is for docrest testing , and Authorization it is real annotation 
 * to be used in project integrated with user-security layer.
 * The <code>Authorization</code> annotation is used to define a list of roles
 * who have access to spring controller http methods.
 * For example:
 * <pre>
 *
 *  
 *   @RequestMapping(value = "admin/oauth2/v1/users/{userId}", method = RequestMethod.GET)
 *   @Authorization(userRoles = { "ROLE_ADMIN","ROLE_BACKOFFIC" } )
 *   public UserAccount getUserDetails(@PathVariable Long userId) {
 *
 *   }
 * </pre>
 * This is the same as
 *  AUTHORIZATION_URI_BUILDER.add(HttpAction.GET , "/api/admin/oauth2/v1/user/*", ROLE_ADMIN, ROLE_BACKOFFICE_ADMIN)
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthorizationTest {
    String[] userRoles();
}
