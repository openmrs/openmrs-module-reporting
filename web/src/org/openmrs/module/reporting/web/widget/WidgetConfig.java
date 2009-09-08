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
package org.openmrs.module.reporting.web.widget;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.web.widget.html.Attribute;

/**
 * Encapsulates the runtime configuration of a Widget
 */
public class WidgetConfig {
	
	// ******* STATIC VARIABLES ********
	protected static final Log log = LogFactory.getLog(WidgetConfig.class);
	
	// ******* PROPERTIES *******
	private HttpServletRequest request; // This represents the Request
	private String id; // This represents the id of the input field
	private String name;  // This represents the name of the input field
	private Class<?> type; // The represents the class to edit. It is an alternative to object/property
	private Type[] genericTypes; // Any generic types on the class
	private Object defaultValue; // This represents a default value for the field
	private String format; // This represents an optional means for rendering a widget
	private Set<Attribute> attributes; // Generic configuration
	
	// ******* CONSTRUCTORS ********
	
	/**
	 * Default Constructor
	 */
	public WidgetConfig() { }
	
	// ******* INSTANCE METHODS ********
	
	/** 
	 * @see Object#clone()
	 */
	@Override
	public WidgetConfig clone() {
		WidgetConfig c = new WidgetConfig();
		c.setRequest(request);
		c.setId(id);
		c.setName(name);
		c.setType(type);
		c.setGenericTypes(genericTypes);
		c.setDefaultValue(defaultValue);
		c.setFormat(format);
		c.setAttributes(attributes);
		return c;
	}
	
	// ******* PROPERTY ACCESS ********
	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	/**
	 * @param request the request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}
	/**
	 * @return the genericTypes
	 */
	public Type[] getGenericTypes() {
		return genericTypes;
	}
	/**
	 * @param genericTypes the genericTypes to set
	 */
	public void setGenericTypes(Type[] genericTypes) {
		this.genericTypes = genericTypes;
	}
	/**
	 * @return the defaultValue
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 * @return the attributes
	 */
	public Set<Attribute> getAttributes() {
		if (attributes == null) {
			attributes = new HashSet<Attribute>();
		}
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Returns the attribute with the given name.
	 * @param name - The attribute name to find
	 * @return - The attribute matching the passed name
	 */
	public Attribute getAttribute(String name) {
		for (Attribute a : getAttributes()) {
			if (a.getName().equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}
	
	/**
	 * @return the value of the attribute with the given name
	 */
	public String getAttributeValue(String name) {
		return getAttributeValue(name, null);
	}
	
	/**
	 * @return the value of the attribute with the given name
	 * @param name the attribute name to get
	 * @param defaultVal the value to return if the attribute is null
	 */
	public String getAttributeValue(String name, String defaultVal) {
		Attribute att = getAttribute(name);
		if (att != null) {
			return att.getValue();
		}
		return defaultVal;
	}
	
	/**
	 * Set Fixed Attribute Value
	 */
	public void setFixedAttribute(String name, String value) {
		Attribute curr = getAttribute(name);
		if (curr != null) {
			curr.setFixedValue(value);
		}
		else {
			getAttributes().add(new Attribute(name, value, null, null));
		}
	}
	
	/**
	 * Set Configured Attribute Value
	 */
	public void setConfiguredAttribute(String name, String value) {
		Attribute curr = getAttribute(name);
		if (curr != null) {
			curr.setConfiguredValue(value);
		}
		else {
			getAttributes().add(new Attribute(name, null, value, null));
		}
	}
	
	/**
	 * Set Default Attribute Value
	 */
	public void setDefaultAttribute(String name, String value) {
		Attribute curr = getAttribute(name);
		if (curr != null) {
			curr.setDefaultValue(value);
		}
		else {
			getAttributes().add(new Attribute(name, null, null, value));
		}
	}
	
	/**
	 * Returns a cloned List of Attributes
	 * @return
	 */
	public List<Attribute> cloneAttributes() {
		List<Attribute> ret = new ArrayList<Attribute>();
		for (Attribute a : getAttributes()) {
			ret.add(new Attribute(a.getName(), a.getFixedValue(), a.getConfiguredValue(), a.getDefaultValue()));
		}
		return ret;
	}
}
