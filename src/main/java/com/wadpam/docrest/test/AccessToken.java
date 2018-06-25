package com.wadpam.docrest.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class AccessToken implements Serializable {

    /** Token type. For now supported: bearer. */
    private final String tokenType = "bearer";

    /** Access token. */
    @JsonProperty("access_token")
    private String accessToken;

    /** Value in milliseconds. */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /** Identifier of user. */
    private Long userId;

    /**mark to identify it is first time login or not used by response 200 or 201*/
    @JsonIgnore
    private Boolean socialFirstTime;
    
    /**extra extraFields to send in response as well. ex HeyGreenGo after authentication it needs to send 
     * quickblox identifier to response the app will need this to interact with quickblox service */
    private Map<String, Object> extraFields;
    
    public AccessToken() {
    }

    public AccessToken(String accessToken, Long expiresIn, Long userId) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @return the access_token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken the access_token to set
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the expires_in
     */
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * @param expiresIn the expires_in to set
     */
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the socialFirstTime
     */
    public Boolean getSocialFirstTime() {
        return socialFirstTime;
    }

    /**
     * @param socialFirstTime the socialFirstTime to set
     */
    public void setSocialFirstTime(Boolean socialFirstTime) {
        this.socialFirstTime = socialFirstTime;
    }

    /**
     * @return the extraFields
     */
    public Map<String, Object> getExtraFields() {
        return extraFields;
    }

    /**
     * @param extraFields the extraFields to set
     */
    public void setExtraFields(Map<String, Object> extraFields) {
        
        this.extraFields = extraFields;
    }
    public void addExtraField(String key, Object value) {
        checkExtraFields();
        extraFields.put(key, value);
    }
    /**
     * @param extraFields the extraFields to set
     */
    public void addExtraFields(Map<String, Object> extraFields) {
        checkExtraFields();
        this.extraFields.putAll(extraFields);
    }
    
    private void checkExtraFields() {
        if (this.extraFields == null) {
            this.extraFields = new HashMap<>();
        }
    }
}
