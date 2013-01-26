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
package org.openmrs.module.reporting.definition.configuration;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Represents a value that may be used to configure a CohortDefinition instance.
 * Typically such a property will be constructed through introspection of
 * objects that contain the {@link ConfigurationProperty} annotation.
 * 
 * @see Property
 * @see EvaluationContext
 */
public class Property implements Serializable {
	
	public static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************
	
	/**
	 * The actual property field that this configuration relates to.
	 */
	private Field field;

	/**
	 * The configured value for this property
	 */
	private Object value;
	
	/**
	 * If true, indicates that this property is required
	 */
	private Boolean required = false;
	
	/**
	 * The display name
	 */
	private String displayName;
	
	/**
	 * The group
	 */
	private String group;
	
	/**
	 * The display format
	 */
	private String displayFormat;
	
	/**
	 * The display attributes.
	 */
	private String displayAttributes;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default constructor
	 */
	public Property() { }
	
	/**
	 * Full constructor for this ConfigurationProperty
	 * 
	 * @param field The field
	 * @param value The configured value for this property
	 * @param required The flag indicating whether a value is required
	 * @param displayName the display name
	 * @param group the group
	 * @param displayFormat the display format
	 * @param displayAttributes the display attributes
	 */
	public Property(Field field, Object value, Boolean required, String displayName, String group, String displayFormat, String displayAttributes) {
		this(field, value, required, displayName, group);
		setDisplayFormat(displayFormat);
		setDisplayAttributes(displayAttributes);
	}
	
	/**
	 * Constructor for this ConfigurationProperty
	 * 
	 * @param field The field
	 * @param value The configured value for this property
	 * @param required The flag indicating whether a value is required
	 * @param displayName the display name
	 * @param group the group
	 */
	public Property(Field field, Object value, Boolean required, String displayName, String group) {
		super();
		setField(field);
		setValue(value);
		setRequired(required);
		setDisplayName(displayName);
		setGroup(group);
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	/**
     * @see Object#toString()
     */
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("ConfigurationProperty<field="+field);
     	sb.append(",value="+(value == null ? "" : value.toString())+",required=" + required+">");
    	return sb.toString();
    }
    
	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Property) {
			Property p = (Property) obj;
			if (this.getField() != null) {
				return (this.getField().equals(p.getField()));
			}
		}
		return this == obj;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (field == null ? 0 : 31 * field.hashCode());
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the required
	 */
	public Boolean getRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the display format
	 */
    public String getDisplayFormat() {
    	return displayFormat;
    }

    /**
	 * @param displayFormat the display format to set
	 */
    public void setDisplayFormat(String displayFormat) {
    	this.displayFormat = displayFormat;
    }

    /**
     * @return the display attributes
     */
    public String getDisplayAttributes() {
    	return displayAttributes;
    }

    /**
	 * @param displayAttributes the display attributes to set
	 */
    public void setDisplayAttributes(String displayAttributes) {
    	this.displayAttributes = displayAttributes;
    }
}
