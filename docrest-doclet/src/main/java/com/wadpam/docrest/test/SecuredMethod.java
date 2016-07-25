package com.wadpam.docrest.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wadpam.docrest.domain.RestCodes;
import com.wadpam.docrest.domain.RestReturn;

/**
 * A stupid controller that test the secured documentation
 * 
 * @author Mattias
 */
@RestReturn
@Controller
@RequestMapping(value = { "{domain}/securemethod" })
public class SecuredMethod {

    /**
     * Get a secured entity.
     * This method is not protected
     * 
     * @param id the id of the secured entity
     * @return JSON representation of the secured entity
     */
    @RestReturn(highlightApiMessage = "get a secured entity", value = Venue.class, 
            codes ={ @RestCodes(codes ="400,403,401,404,500")})
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<Venue> get(@PathVariable String id) {
        Venue venue = new Venue();
        return new ResponseEntity<Venue>(venue, HttpStatus.OK);
    }

    /**
     * Delete a secured entity.
     * this method is protected
     * 
     * @param id the id of the secured entity to delete
     * @return 200 if deleted successfully, empty body
     */
    @RestReturn(highlightApiMessage = "delete a secured entity.",value = Void.class, isSecured = true,
            codes ={ @RestCodes(codes ="400,403,401,404,500")})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String id) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
