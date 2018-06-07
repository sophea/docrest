package com.wadpam.docrest.test;

import java.util.Date;
import java.util.List;

/**
 * @author Mattias
 */
public class Child extends AbstractLongDomainEntity {

    private Long id;

    private String name;

    private Date dateOfBirth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return the id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /** this will show in the json */
    public String getExtraField() {
        return "";
    }

    /** this will show in the json */
    public boolean isExtraFieldBoolean() {
        return false;
    }

    /** this will show in the json */
    public List<Content> getContent() {
        return null;
    }
}
