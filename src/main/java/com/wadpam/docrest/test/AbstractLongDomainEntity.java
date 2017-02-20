package com.wadpam.docrest.test;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author sophea
 */
public abstract class AbstractLongDomainEntity  {
    
    /**
     * 
     */
    private static final long serialVersionUID = -1132052010567240470L;
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_VERSION = "version";
    public static final String COLUMN_NAME_STATE = "state";
    /**unique id*/
    private Long id;
    /**version number changes */
    private Long version;
    /**state */
    private Long state;
    /**created by*/
    @JsonIgnore
    private String createdBy;
    /**created date*/
    @JsonIgnore
    private Date createdDate;
    /**updated by*/
    @JsonIgnore
    private String updatedBy;
    /**updated date*/
    private Date updatedDate;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}
