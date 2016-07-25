/**
 * 
 */
package com.wadpam.docrest.test;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author
 */

public class Content implements Serializable {

    private static final long serialVersionUID = 1459403664947376442L;
    private Integer originalQty = 0;
    private Integer remainingQty = 0;

    private Content product;
    
    private String aaaa;
    private String bbb;

    private List<Content> items;
    private List<String> strings;
    private Date date;
    /**
     * @return the originalQty
     */
    public Integer getOriginalQty() {
        return originalQty;
    }

    /**
     * @param originalQty the originalQty to set
     */
    public void setOriginalQty(Integer originalQty) {
        this.originalQty = originalQty;
    }

    /**
     * @return the remainingQty
     */
    public Integer getRemainingQty() {
        return remainingQty;
    }

    /**
     * @param remainingQty the remainingQty to set
     */
    public void setRemainingQty(Integer remainingQty) {
        this.remainingQty = remainingQty;
    }

    /**
     * @return the product
     */
    public Content getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(Content product) {
        this.product = product;
    }

   
    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the aaaa
     */
    public String getAaaa() {
        return aaaa;
    }

    /**
     * @param aaaa the aaaa to set
     */
    public void setAaaa(String aaaa) {
        this.aaaa = aaaa;
    }

    /**
     * @return the bbb
     */
    public String getBbb() {
        return bbb;
    }

    /**
     * @param bbb the bbb to set
     */
    public void setBbb(String bbb) {
        this.bbb = bbb;
    }

    /**
     * @return the items
     */
    public List<Content> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<Content> items) {
        this.items = items;
    }

    /**
     * @return the strings
     */
    public List<String> getStrings() {
        return strings;
    }

    /**
     * @param strings the strings to set
     */
    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

}
