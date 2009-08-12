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
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.jsp.JspException;
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
	
	// ******* CONSTRUCTORS ********
	
	/**
	 * Default Constructor
	 */
	public WidgetTag() {
		super();
	}
	
	// ******* INSTANCE METHODS ********
	
	/**
	 * @see TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		
		// Retrieve the type, depending on either an object/property combination or a clazz type
		Class<?> type = null;
		Type[] genericTypes = null;
		Object propertyValue = null;
		
		if (getObject() != null && getProperty() != null) {
			
			Field f = ReflectionUtil.getField(getObject().getClass(), getProperty());
			type = ReflectionUtil.getFieldType(f);
			genericTypes = ReflectionUtil.getGenericTypes(f);
			propertyValue = ReflectionUtil.getPropertyValue(object, property);
			
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
		
		// Ensure that we have an appropriate Handler
		WidgetHandler handler = HandlerUtil.getPreferredHandler(WidgetHandler.class, type);
		if (handler == null) {
			throw new JspException("No Preferred Handler found for: " + type);
		}
		
		// Setup Widget Configuration
		WidgetConfig config = new WidgetConfig();
		config.setPageContext(pageContext);
		config.setId(getId());
		config.setName(getName());
		config.setType(type);
		config.setGenericTypes(genericTypes);
		config.setFormat(getFormat());
		config.setDefaultValue(propertyValue != null ? propertyValue : getDefaultValue());
		
		config.setFixedAttribute("id", getId());
		config.setFixedAttribute("name", getName());
		if (getAttributes() != null) {
			Map<String, String> attMap = OpenmrsUtil.parseParameterList(getAttributes());
			for (String attName : attMap.keySet()) {
				config.setConfiguredAttribute(attName, attMap.get(attName));
			}
		}
		
		// Run the Handler with this Configuration 
		try {
			handler.render(config);
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
	 * @param property the property to set
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
	public String getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
}
