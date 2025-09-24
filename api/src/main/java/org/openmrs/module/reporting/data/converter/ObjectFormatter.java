/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import java.util.Locale;

import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Formats any object into a String representation
 */
public class ObjectFormatter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private String specification;

    private Locale locale;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public ObjectFormatter() {}
	
	/**
	 * Constructor with specification
	 */
	public ObjectFormatter(String specification) {
		this.specification = specification;
	}

    /**
     * Constructor with locale
     *
     * @param locale
     */
    public ObjectFormatter(Locale locale) {
        this.locale = locale;
    }

    /**
     * Constructor with specification and locale
     *
     * @param specification
     * @param locale
     */
    public ObjectFormatter(String specification, Locale locale) {
        this.specification = specification;
        this.locale = locale;
    }

	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#converter(Object)
	 * @should convert an Object into a nicely formatted text representation
	 */
	public Object convert(Object o) {
		return ObjectUtil.format(o, getSpecification(), getLocale());
	}
	
	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Object.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the specification
	 */
	public String getSpecification() {
		return specification;
	}

	/**
	 * @param specification the specification to set
	 */
	public void setSpecification(String specification) {
		this.specification = specification;
	}

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
