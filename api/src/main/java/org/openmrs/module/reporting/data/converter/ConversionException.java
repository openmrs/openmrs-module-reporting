package org.openmrs.module.reporting.data.converter;

import org.openmrs.api.APIException;

/*Represents an Exception thrown during conversion*/
public class ConversionException extends APIException {
	public static final long serialVersionUID = 1L;

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(String message, Throwable t) {
		super(message, t);
	}
}
