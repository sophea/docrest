/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wadpam.docrest.domain.RestCodes;
import com.wadpam.docrest.domain.RestReturn;

/**
 * @author os
 */
public class AbstractController<T extends AbstractLongDomainEntity> {

    /**
     * Retrieves all entities of type T
     * 
     * @param offset skip this number of entities before returning. Used for paging. Default is 0.
     * @param limit return this number of entities. Used for paging. Default is 10.
     * @return
     */
    // @RestReturn(value=List.class)
    // @RequestMapping(value="", method= RequestMethod.GET)
    // public ResponseEntity<List<T>> findAll(@RequestParam(defaultValue="0") int offset,
    // @RequestParam(defaultValue="10") int limit) {
    // return new ResponseEntity<List<T>>(HttpStatus.NOT_FOUND);
    // }

    @RestReturn( highlightApiMessage = "Retrieves all entities of type T", value = List.class)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<T>> findAll(@RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        return new ResponseEntity<List<T>>(HttpStatus.NOT_FOUND);
    }

    /**
     * Lee's post test, Retrieves all entities of type T
     * 
     * @param offset skip this number of entities before returning. Used for paging. Default is 0.
     * @param limit return this number of entities. Used for paging. Default is 10.
     * @return
     */
    @RestReturn( highlightApiMessage = "Retrieves all entities of type T", value = List.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<List<T>> findAllPost(@RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        return new ResponseEntity<List<T>>(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieves the first entity of type T
     * @param genericEntiry
     * @param domainName
     * @param offerId
     * @return
     */
    @RestReturn(highlightApiMessage = "create with id",
           value = Object.class, entity= Object.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "created/{id}", method = RequestMethod.POST)
    public ResponseEntity<T> create(@PathVariable(value = "domain") String domainName, @ModelAttribute T genericEntity,
            @PathVariable("id") Long offerId) {
        return new ResponseEntity<T>(genericEntity, HttpStatus.OK);
    }
    
    /**
     * create with json object
     * @param id
     * @param genericEntity
     * @return
     */
    @RestReturn(highlightApiMessage = "create with json object",
           value = Object.class, entity= Object.class,
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "created/{id}/json", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> createWithJson(@PathVariable(value = "id") String id, @RequestBody T genericEntity) {
        return new ResponseEntity<T>(genericEntity, HttpStatus.OK);
    }
    
    /**
     * Get first entity
     * @param id
     * @param genericEntity
     * @return
     */
    @RestReturn(highlightApiMessage = "get first entity", value = ResponseEntity.class,
     codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value = "1st", method = RequestMethod.GET)
    public ResponseEntity<T> first(@ModelAttribute Child person) {
        return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
    }

}
