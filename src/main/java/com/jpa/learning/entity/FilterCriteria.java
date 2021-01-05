package com.jpa.learning.entity;

import java.io.Serializable;

/**
 * Holds terminal selection criteria
 * 
 * @author pn250091
 *
 */
public class FilterCriteria implements Serializable {

    /**
     * Serial id
     */
    private static final long serialVersionUID = 1L;

    /**
     * Filter on field
     */
    private String key;

    /**
     * Defines the operators used for terminal criteria searches.
     */
    private String operation;
    
    private String type;
    /**
     * Value to match with
     */
    private Object value;

    /**
     * Default constructor
     */
    public FilterCriteria() {
    }

    /**
     * Parameterized constructor to set key, value and initialize
     * categoryFilterDataList
     */
    public FilterCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }
    
    public FilterCriteria(String key, String operation, String type, Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.type = type;
        this.value = value;
    }

    /**
     * The filter key
     * 
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * It sets the filter key For example name,component,etc
     * 
     * @param key
     *            to set value to
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * The filter operation for example contains,eq,etc
     * 
     * @return operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * It sets the filter operation for example contains,eq,etc
     * 
     * @param operation
     *            to set value to
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * The filter key value
     * 
     * @return value
     */
    public Object getValue() {
        return value;
    }

    /**
     * It sets the value to match with
     *
     * @param value
     *            to set value to
     */
    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}