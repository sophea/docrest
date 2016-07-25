/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.test;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author os
 */
@JsonIgnoreProperties(value = {"skippedProperty1", "skippedProperty2"})
public class Venue extends AbstractLongDomainEntity {

    
    private String name;
    
    public static String testName;
    
    private Collection<VenueParent> venueParent;

	private String skippedProperty1;

	private String skippedProperty2;
    
    public Collection<VenueParent> getVenueParent() {
		return venueParent;
	}

	public void setVenueParent(Collection<VenueParent> venueParent) {
		this.venueParent = venueParent;
	}

	@JsonIgnore
    private int establishedYear;
    
    private Location location;
    
    public static String getTestName(){
    	return testName;
    }


    public int getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(int establishedYear) {
        this.establishedYear = establishedYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

	public String getSkippedProperty1() {
		return skippedProperty1;
	}

	public void setSkippedProperty1(final String skippedProperty1) {
		this.skippedProperty1 = skippedProperty1;
	}

	public String getSkippedProperty2() {
		return skippedProperty2;
	}

	public void setSkippedProperty2(final String skippedProperty2) {
		this.skippedProperty2 = skippedProperty2;
	}
}
