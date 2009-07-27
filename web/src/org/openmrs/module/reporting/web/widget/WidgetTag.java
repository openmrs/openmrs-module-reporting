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
	private Class<?> clazz; // The represents the clazz to edit. It is an alternative to object/property
	private Object defaultValue; // This represents a default value for the field
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
	 * Returns the defined type for this tag
	 * @return
	 */
	public Class<?> getType() {
		Class<?> type = null;
		if (getObject() != null && getProperty() != null) {
			Field f = ReflectionUtil.getField(object.getClass(), property);
			type = ReflectionUtil.getFieldType(f);
			if (type == null) {
				throw new IllegalArgumentException("Property <" + property + "> is invalid for object <" + object + ">");
			}
			if (getClazz() != null && getClazz() != type) {
				throw new IllegalArgumentException("The specified clazz is not compatible with the specified property.");
			}
		}
		else if (getClazz() != null) {
			type = getClazz();
		}
		else {
			throw new IllegalArgumentException("You must specify an object/property or clazz attribute.");
		}
		return type;
	}
	
	/**
	 * @see TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		
		// This tag allows you to specify either an object/property combination or a clazz type
		Class<?> type = getType();
		WidgetHandler h = HandlerUtil.getPreferredHandler(WidgetHandler.class, type);
		if (h == null) {
			throw new JspException("No Handler found for: " + type);
		}
		try {
			h.handle(this);
		}
		catch (Exception e) {
			throw new JspException("Error handling Widget: " + type, e);
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
		setClazz(null);
		setDefaultValue(null);
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
	 * @return the clazz
	 */
	public Class<?> getClazz() {
		return clazz;
	}
	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	/**
	 * If the widget is configured with an object and a property, and the
	 * property value is not null, this returns it.  Otherwise, it returns 
	 * the defaultValue property.
	 */
	public Object getDefaultValue() {
		if (object != null && property != null) {
			Object val = ReflectionUtil.getPropertyValue(object, property);
			if (val != null) {
				return val;
			}
		}
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
