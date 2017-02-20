/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wadpam.docrest.domain.RestCodes;
import com.wadpam.docrest.domain.RestReturn;

/**
 * @author os
 */
@RestReturn(entity = Content.class )
@Controller
@RequestMapping(value = { "{domain}/content", "{domain}/v10/contents" })
public class TestController {
    
    /**
     * create an entity with form
     * @Param body
     * @return
     */
    @RestReturn(highlightApiMessage = "create an entity with form", value = Content.class, 
            codes = { @RestCodes(codes = "401, 403,200, 500") }, supportsClassParams = true)
    @RequestMapping(value="content", method = {RequestMethod.POST})
    public ResponseEntity<Content> content(@ModelAttribute Content body) {
        return new ResponseEntity<Content>(HttpStatus.NOT_FOUND);
    }
    
    /**
     * create an entity with json
     * @Param body
     * @return
     */
    @RestReturn(highlightApiMessage = "create an entity with json", value = Content.class,
            codes = {@RestCodes(codes="400,200,403,404,500")}, supportsClassParams = true)
    @RequestMapping(value="content/json", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Content> content1( @RequestBody Content body) {
        return new ResponseEntity<Content>(body,HttpStatus.OK);
    }
    
    /**
     * get content by id
     * <p>
     * Verifies user account login with type resources owner password. <br/>
     * To pass this request you need to pass authentication by user type credentials first otherwise response back 401.<br/>
     *
     * There is one listener added in this API, there are 2 two methods (pre & after Process) to be invoked. <br/><br/>
     * 1 -preProcess() : may be used for validation request params or other purposes <br/>
     * 2 -afterPorcess() :  after login successfully, this method will be called and backend project <br/><br/>
     *
     * <ul>
     * <li> create a new Listener ex: (Oauth2LoginListener) implemented from  UserListener</li>
     * <li> implement on preProcess & afterProcess methods
     * <li> declare OAuth2Controller as autoWired tag in MvcConfig or you can do in xml configuration
     * <li> set listener in securityModule() method: OAuth2Controller.setListener(new Oauth2LoginListener());</li>
     * <li> see more details in template-gae-backend or template-backend
     * </ul>

     *
     * If you need to enable this listener, you may implement this listener in your backend integration. <br/>
     *  How to integration from backend project :<br/>
     *  </p>
     * @param id content id
     * @return
    */
    @RestReturn(highlightApiMessage = "get content by id", value = Content.class,
            codes = {@RestCodes(codes="400,200,403,404,500")}, supportsClassParams = true)
    @RequestMapping(value="content/{id}", method = {RequestMethod.GET})
    public ResponseEntity<Content> content2(@PathVariable Long id) {
        Content body = new Content();
        return new ResponseEntity<Content>(body,HttpStatus.OK);
    }
    
    
    
}
