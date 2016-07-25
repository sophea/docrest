/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.sun.javadoc.ClassDoc;

/**
 *
 * @author os
 */
public class Resource {
private String[] paths;
    
    private String entityType;
    
    private String simpleType;
    
    private ClassDoc classDoc;
    
    private Map<String, Collection<Method>> operationsMap = new TreeMap<String, Collection<Method>>();
    
    private final Set<Method> methods = new TreeSet<Method>();

    private int count;

    private String name;

    // Traits that will be presented as badges on the documentation
    // E.g. Secured
    private List<String> traits = new ArrayList<String>();

    private boolean includeApi = false;

    private List<String> classHierarchy;

    public void addTrait(String trait) {
        if (!traits.contains(trait)) {
            traits.add(trait);
        }
    }

    public void removeTrait(String trait) {
        traits.remove(trait);
    }

    public boolean hasTrait(String trait) {
        return traits.contains(trait);
    }

    @Override
    public String toString() {
        return name + ", " + simpleType;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIncludeApi() {
        return includeApi;
    }

    public void setIncludeApi(boolean includeApi) {
        this.includeApi = includeApi;
    }

    public ClassDoc getClassDoc() {
        return classDoc;
    }

    public void setClassDoc(ClassDoc classDoc) {
        this.classDoc = classDoc;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getSimpleType() {
        return simpleType;
    }

    public void setSimpleType(String simpleType) {
        this.simpleType = simpleType;
    }

    public Map<String, Collection<Method>> getOperationsMap() {
        return operationsMap;
    }

    public void setOperationsMap(Map<String, Collection<Method>> operationsMap) {
        this.operationsMap = operationsMap;
    }

    public List<String> getClassHierarchy() {
        return classHierarchy;
    }

    public void setClassHierarchy(List<String> classHierarchy) {
        this.classHierarchy = classHierarchy;
    }

    public List<String> getTraits() {
        return traits;
    }

    public void setTraits(List<String> traits) {
        this.traits = traits;
    }
}
