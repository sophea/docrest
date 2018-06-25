package com.wadpam.docrest.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wadpam.docrest.domain.RestCodes;
import com.wadpam.docrest.domain.RestReturn;

/**
 * Child resource.
 *
 * @author Mattias
 */
@RestReturn(isSecured = true)
@RestController
@RequestMapping(value = { "child" })
public class ChildController extends CrudController {



    @RestReturn(highlightApiMessage = "exchange accessToken", value=AccessToken.class, entity=AccessToken.class, codes = {
            @RestCodes(codes = "401, 403,200, 500")}, supportsClassParams = true)
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @AuthorizationTest(userRoles = {"ROLE_USER"})
    public ResponseEntity<AccessToken> pathSomethod(@RequestParam String username, @RequestParam String password) {

        return new ResponseEntity<>(new AccessToken(), HttpStatus.OK);
    }

    /**
     * Delete a child entity.
     * this method is protected
     *
     * @param id the id of the child entity to delete
     * @return 200 if deleted successfully, empty body
     */
    @RestReturn(highlightApiMessage = "delete user with id", value = Child.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String id) {
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Get user with id
     * this method is not protected
     *
     * @param id the id of the user entity
     * @return 200 if get successfully, empty body
     */
    @RestReturn(highlightApiMessage = "get user with id", value = Child.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "admin/oauth2/v1/user/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Child> getUser(@PathVariable String id) {
        Child child = new Child();
        return new ResponseEntity<Child>(child, HttpStatus.OK);
    }

    /**
     * Get entity detail with id
     * this method is not protected
     *
     * @param id the id of the user entity
     * @return 200 if get successfully, empty body
     */
    @RestReturn(highlightApiMessage = "get entity detail with id", value = Child.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "admin/oauth2/v1/user/{userId}", method = RequestMethod.POST, consumes =  {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Child> getUserDetails(@PathVariable String id) {
        Child child = new Child();
        return new ResponseEntity<Child>(child, HttpStatus.OK);
    }

    /**
     * Get a child entity.
     * This method is not protected
     *
     * @param id the id of the child entity
     * @return JSON representation of the child entity
     */
    @RestReturn(highlightApiMessage = "get entity with get", value = Child.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<Child> get(@PathVariable String id) {
        Child child = new Child();
        return new ResponseEntity<Child>(child, HttpStatus.OK);
    }

    /**
     * Get entity with post
     * this method is not protected
     *
     * @param id the id of the user entity
     * @return 200 if get successfully, empty body
     */
    @RestReturn(highlightApiMessage = "get entity with post", value = Child.class, entity = Child.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "admin/oauth2/v1/user/{id}", method = RequestMethod.POST)
    public ResponseEntity<Child> getUserWithPost(@PathVariable String id) {
        Child child = new Child();
        return new ResponseEntity<Child>(child, HttpStatus.OK);
    }

    /**
     * Update entity with json\"
     * this method is not protected
     * @param body child object
     * @param id the id of the user entity
     * @return 200 if get successfully, empty body
     */
    @RestReturn(highlightApiMessage = "update entity with json", value = Child.class, entity = Child.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "admin/oauth2/v1/update", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Child> updateUser(@RequestBody Child body) {
        Child child = new Child();
        return new ResponseEntity<Child>(child, HttpStatus.OK);
    }

    /**
     * Get first method
     * this method is not protected
     *
     * @param id the id of the user entity
     * @return 200 if get successfully, empty body
     */
    @RestReturn(highlightApiMessage = "get first method", value = Child.class, entity = Child.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "1st/{id}", method = RequestMethod.GET)
    public ResponseEntity<Child> getFirstMethod(@PathVariable String id) { // 5
        Child child = new Child();
        return new ResponseEntity<Child>(child, HttpStatus.OK);
    }

}
