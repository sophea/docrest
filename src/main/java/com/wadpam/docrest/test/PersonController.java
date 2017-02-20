/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wadpam.docrest.domain.RestCodes;
import com.wadpam.docrest.domain.RestReturn;

/**
 * Comment on First Controller will be api guideline </br>
 * This is the PersonController javadoc text, containing a few <b>HTML tags</b>.
 * 
 * @param stores
 *            optional inner object. boolean
 * @author os
 */
@RestReturn(value = Child.class)
@ControllerAdvice
@RequestMapping(value = "{domain}")
public class PersonController extends AbstractController<Child> {

    /**
     * Test when RestReturn.value and RestReturn.entity are not explicitly listed so their default values are used instead.
     * @param pagesize default page is 10
     * @return response entity
     */
    @RestReturn(highlightApiMessage = "test default entity.",
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "defaultentity", method = RequestMethod.GET)
    public ResponseEntity testDefaultEntity(@RequestParam(defaultValue = "11") int pagesize) {
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Test default entity.
     * @return response entity
     */
    @RestReturn(highlightApiMessage = "test default entity.",
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "defaultentity", method = RequestMethod.GET)
    public ResponseEntity testDefaultEntity() {
        return new ResponseEntity(HttpStatus.OK);
    }

    // @RestReturn(codes={@RestCodes(codes="200")})
    // @RequestMapping(value = "defaultentity", method = RequestMethod.GET)
    // public ResponseEntity testDefaultEntity() {
    // return new ResponseEntity(HttpStatus.OK);
    // }

    /**
     * FindByName returns all Venues with the specified name
     * @param name the specified name
     * @param position 
     * @param test
     * @return all Venues with the specified name
     * @since 1.23
     */
    @RestReturn(highlightApiMessage = "findByName returns all Venues with the specified name", value = Venue.class,
    codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "{name}/v2", method = RequestMethod.GET)
    public ResponseEntity<Venue> findByName1(@PathVariable String name,
            @RequestParam(value = "p1", defaultValue = "staff") String position, @RequestBody(required = true) Boolean test) {

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    
    /**
     * Find by type
     * @param type 
     * @return 
     */
    @RestReturn(highlightApiMessage = "find by type", value = Venue.class,
    codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "v1", method = RequestMethod.GET)
    public ResponseEntity<Venue> findByType(@RequestParam Long type) {

        return new ResponseEntity<Venue>(HttpStatus.OK);
    }

    /**
     * Find by state
     * @param state 
     * @return 
     */
    @RestReturn(highlightApiMessage = "find by state", value = Venue.class,
    codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "v1", method = RequestMethod.GET)
    public ResponseEntity<Venue> findByState(@RequestParam Long state) {

        return new ResponseEntity<Venue>(HttpStatus.OK);
    }
    
    /**
     * FindByName returns all Venues with the specified name
     * @param name the specified name
     * @return all Venues with the specified name
     * @since 1.23
     */
    @RestReturn(highlightApiMessage = "findByName returns all Venues with the specified name", value = Venue.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "{name}", method = RequestMethod.GET)
    public ResponseEntity<Venue> findByName(@PathVariable String name) {
        return new ResponseEntity<Venue>(HttpStatus.NOT_FOUND);
    }
    
    /**
     * Method comment for delete with id
     * @param id 
     * @return
     */
    @RestReturn(highlightApiMessage = "method comment for delete with id", value = Venue.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Venue> delete(@PathVariable String id) {

        return new ResponseEntity<Venue>(HttpStatus.OK);
    }

    /**
     * Method comment for update with ModelAttributes
     * @param person this is the javadoc comment for person ModelAttribute
     * @param location this is the javadoc for location ModelAttribute
     * @return
     */
    @RestReturn(highlightApiMessage = "method comment for update with ModelAttributes", value = Venue.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "{name}", method = RequestMethod.POST)
    public ResponseEntity<Venue> update(@ModelAttribute Venue person, @ModelAttribute Location location) {
        return null;
    }

    /**
     * Method comment for update with @RequestBody
     * @param body
     * @return 
     */
    @RestReturn(highlightApiMessage = "method comment for update with body", value = String.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "{name}/v2", method = RequestMethod.POST)
    public ResponseEntity<String> updateAsRequestBody(@RequestBody Test test) {
        return null;
    }
}
