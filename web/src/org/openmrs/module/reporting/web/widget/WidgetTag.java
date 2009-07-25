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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.web.widget.handler.WidgetHandler;
import org.openmrs.module.util.ReflectionUtil;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsUtil;

/**
 * Renders an appropriate form field Widget
 */
public class WidgetTag extends TagSupport {
	
	// ******* STATIC VARIABLES ********
	
	public static final long serialVersionUID = 21132L;
	protected static final Log log = LogFactory.getLog(WidgetTag.class);
	
	// ******* PROPERTIES *******
	private String id; // This represents the id of the input field
	private String name;  // This represents the name of the input field
	private Object object; // This represents the object whose property we are editing
	private String property; // This represents the name of the property/field to edit on the object
	private String format; // This represents an optional means for rendering a widget
	private String attributes; // Pipe-separated list of attributes to provide flexible control of Widgets as needed
	
	// ******* PRIVATE INSTANCE VARIABLES ********
	private Map<String, String> attributeMap = new HashMap<String, String>();
	
	// ******* CONSTRUCTORS ********
	
	/**
	 * Default Constructor
	 */
	public WidgetTag() {
		super();
	}
	
	// ******* INSTANCE METHODS ********
	
	/**
	 * Returns the PageContext for this Tag
	 */
	public PageContext getPageContext() {
		return pageContext;
	}
	
	/** 
	 * Gets the matching field on the passed Object
	 */
	public Field getField() {
		Field f = ReflectionUtil.getField(object.getClass(), property);
		if (f != null) {
			return f;
		}
		throw new IllegalArgumentException("Property <" + property + "> is invalid for object <" + object + ">");
	}
	
	/**
	 * Gets the default value of the property on the passed Object
	 */
	public Object getDefaultValue() {
		return ReflectionUtil.getPropertyValue(object, property);
	}
	
	/**
	 * @see TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		WidgetHandler h = HandlerUtil.getPreferredHandler(WidgetHandler.class, ReflectionUtil.getFieldType(getField()));
		if (h == null) {
			throw new JspException("No Handler found for: " + getField().getType());
		}
		try {
			h.handle(this);
		}
		catch (Exception e) {
			throw new JspException("Error handling Widget: " + getField().getType(), e);
		}
		resetValues();
		return SKIP_BODY;
	}
	
	/**
	 * Resets the properties of the tag
	 */
	protected void resetValues() {
		setId(null);
		setName(null);
		setObject(null);
		setProperty(null);
		setFormat(null);
		setAttributes(null);
	}
	
	// ******** UTILITY METHODS ********
	
	/**
	 * Utility method to retrieve a named attribute
	 */
	protected String getAttribute(String name) {
		return attributeMap.get(name);
	}
	
	// ******* PROPERTY ACCESS ********
	
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
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}
	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}
	/**
	 * @param propertyName the propertyName to set
	 */
	public void setProperty(String property) {
		this.property = property;
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
	public String getAttributes() {
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(String attributes) {
		this.attributes = attributes;
		if (attributes != null) {
			attributeMap = OpenmrsUtil.parseParameterList(attributes);
		}
		else {
			attributeMap = new HashMap<String, String>();
		}
	}
	
	/**
	 * Return a Map of the passed attributes
	 */
	public Map<String, String> getAttributeMap() {
		return attributeMap;
	}
	
	/**
	 * Returns the attribute with the given name.
	 * If null, returns the passed default value
	 * @param name - The attribute name to find
	 * @param defaultValue - The value to return if the attribute is null
	 * @return - The attribute matching the passed name, or defaultValue if null
	 */
	public String getAttribute(String name, String defaultValue) {
		if (getAttributeMap() != null) {
			String att = getAttributeMap().get(name);
			if (att != null) {
				return att;
			}
		}
		return defaultValue;
	}
}
