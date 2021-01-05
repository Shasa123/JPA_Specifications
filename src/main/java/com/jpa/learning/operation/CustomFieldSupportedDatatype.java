package com.jpa.learning.operation;

public enum CustomFieldSupportedDatatype {

    DATE("date"),
    STRING("string"),
    INTEGER("integer");
    
    private String message;

    private CustomFieldSupportedDatatype(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    
}
