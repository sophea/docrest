/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 *
 * @author os
 */
public class Method implements Comparable<Method>, Trait {

    public Method(Resource resource) {
        this.resource = resource;
    }

    private final Resource resource;

    private String paths[];

    private String method = "*";

    private String name;

    private ClassDoc classDoc;

    private MethodDoc methodDoc;

    private List<Param> pathVariables = new ArrayList<Param>();

    private List<Param> modelAttributes = new ArrayList<Param>();

    private List<Param> parameters = new ArrayList<Param>();

    private AnnotationDesc restReturn;

    private String returnType;

    private String entityType;
    
    private String highlightApiMessage;

    private String contentType = "";
    
    // @RestReturn.value
    private String json;

    // @RestReturn.entity
    private String jsonEntity;

    private Param body = null;

    private boolean supportsClassParams = false;

    // Traits that will be presented as badges on the documentation
    // E.g. Secured
    private List<String> traits = new ArrayList<String>();

    @Override
    public int compareTo(Method t) {
    	String[] str1 = paths[0].split("/");
    	String[] str2 = t.paths[0].split("/");
    	if(str1.length != str2.length){
    		if(str1.length > str2.length){
    			return 1;
    		}else{
    			return -1;
    		}
    	}else if(!paths[0].replaceAll("\\{", "").replaceAll("\\}", "").trim().
    			equalsIgnoreCase(t.paths[0].replaceAll("\\{", "").replaceAll("\\}", "").trim())){	
    		
    		return paths[0].replaceAll("\\{", "").replaceAll("\\}", "").trim().
    				compareToIgnoreCase(t.paths[0].replaceAll("\\{", "").replaceAll("\\}", "").trim());
    	
    	}else if(!method.equals(t.method)){
    		return method.compareToIgnoreCase(t.method);
    	}	
    	return -1;
    }

    @Override
    public void addTrait(String trait) {
        if (!traits.contains(trait)) {
            traits.add(trait);
        }
    }

    @Override
    public void removeTrait(String trait) {
        traits.remove(trait);
    }

    @Override
    public boolean hasTrait(String trait) {
        return traits.contains(trait);
    }


    // Getters and setters
    
    public ClassDoc getClassDoc() {
        return classDoc;
    }

    public void setClassDoc(ClassDoc classDoc) {
        this.classDoc = classDoc;
    }

    public MethodDoc getMethodDoc() {
        return methodDoc;
    }

    public void setMethodDoc(MethodDoc methodDoc) {
        this.methodDoc = methodDoc;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String paths[]) {
        this.paths = paths;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Param> getParameters() {
        return parameters;
    }

    public void setParameters(List<Param> parameters) {
        this.parameters = parameters;
    }

    public List<Param> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(List<Param> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getJsonEntity() {
        return jsonEntity;
    }

    public void setJsonEntity(String jsonEntity) {
        this.jsonEntity = jsonEntity;
    }

    public AnnotationDesc getRestReturn() {
        return restReturn;
    }

    public void setRestReturn(AnnotationDesc restReturn) {
        this.restReturn = restReturn;
    }

    public String getEntityType() {
        if ((null == entityType || Object.class.getName().equals(entityType)) && 
                !Object.class.getName().equals(resource.getEntityType())) {
            return resource.getEntityType();
        }
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getReturnType() {
        String returnValue = returnType;
        if (null != returnType
                && (returnType.startsWith(List.class.getName()) || returnType.startsWith(Collection.class.getName()))) {
            returnValue = String.format("%s<%s>", returnType, getEntityType());
        }
        return returnValue;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Resource getResource() {
        return resource;
    }

    public Param getBody() {
        return body;
    }

    public void setBody(Param body) {
        this.body = body;
    }

    public List<Param> getModelAttributes() {
        return modelAttributes;
    }

    public void setModelAttributes(List<Param> modelAttributes) {
        this.modelAttributes = modelAttributes;
    }

    public boolean isSupportsClassParams() {
        return supportsClassParams;
    }

    public void setSupportsClassParams(boolean supportsClassParams) {
        this.supportsClassParams = supportsClassParams;
    }

    public List<String> getTraits() {
        return traits;
    }

    public void setTraits(List<String> traits) {
        this.traits = traits;
    }

    /**
     * @return the highlightApiMessage
     */
    public String getHighlightApiMessage() {
        return highlightApiMessage;
    }

    /**
     * @param highlightApiMessage the highlightApiMessage to set
     */
    public void setHighlightApiMessage(String highlightApiMessage) {
        this.highlightApiMessage = highlightApiMessage;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    
}
