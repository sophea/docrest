package com.wadpam.docrest.domain;

/**
 *
 * @author Mattias
 */
public interface Trait {

    void addTrait(String trait);

    void removeTrait(String trait);

    boolean hasTrait(String trait);
}
