package com.ecommerce.project.exception;

public class ResourceNotFoundException extends RuntimeException {

    String resourceName;
    String field;
    String fieldName;
    Long fieldId;

    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
       super(String.format("Resource %s not found for field %s and for field name is %s", resourceName, field,fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException(String field, String resourceName, Long fieldId) {

        super(String.format("Resource %s not found for field %s for %s", resourceName, field,fieldId));
        this.field = field;
        this.resourceName = resourceName;
        this.fieldId = fieldId;
    }

    public ResourceNotFoundException() {
    }
}
