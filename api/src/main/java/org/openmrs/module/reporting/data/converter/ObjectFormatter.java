/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.data.converter;

import java.util.Locale;

import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Formats any object into a String representation
 */
public class ObjectFormatter extends DataConverterBase {
	
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
	 * @see DataConverterBase#convertObject(Object) (Object)
	 * @should convert an Object into a nicely formatted text representation
	 */
	protected Object convertObject(Object o) {
		return ObjectUtil.format(o, getSpecification(), getLocale());
	}
	
	/** 
	 * @see DataConverterBase#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}
	
	/** 
	 * @see DataConverterBase#getInputDataType()
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
