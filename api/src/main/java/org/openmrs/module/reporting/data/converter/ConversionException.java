package org.openmrs.module.reporting.data.converter;

public class ConversionException extends RuntimeException {

    public ConversionException(Object object, Class converterClass, Exception e) {
        super(String.format("'%s' is unable to convert object of type '%s'. Nested exception is: '%s'", converterClass, object.getClass(), e.getMessage()));
    }
}
