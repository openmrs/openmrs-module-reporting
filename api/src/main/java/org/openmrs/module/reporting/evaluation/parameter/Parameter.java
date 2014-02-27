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
package org.openmrs.module.reporting.evaluation.parameter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * A Parameter represents a value that may be used to configure an Object at runtime.
 * Examples of classes where Parameters are used are ReportDefinition, CohortDefinition, and DataSetDefinition
 * If an object is parameterizable, it is able to accept a parameter like this. 
 * Examples of a parameter would be "What start date do you want to use?"
 * Typically Parameter Values are retrieved in the context of an {@link EvaluationContext}
 * 
 * @see Parameterizable
 * @see EvaluationContext
 */
@SuppressWarnings("rawtypes")
public class Parameter implements Serializable {
	
	public static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************
	
	/**
	 * The name by which this Parameter can be uniquely retrieved
	 * in the Context within which it is being used.
	 */
	private String name;
	
	/**
	 * The text displayed to the user if input is needed
	 */
	private String label;
	
	/**
	 * The datatype of this parameter
	 */
	private Class<?> type;
	
	/**
	 * If this parameter can have multiple values specified, this is the underlying Collection type
	 */
	private Class<? extends Collection> collectionType;

	/**
	 * The default value given to this parameter.
	 */
	private Object defaultValue;

	/**
	 * Indicates whether this parameter is required, or should accept a value of null.  Default to required = true.
	 */
	private boolean required = true;
	
	/**
	 * Configuration properties that will be sent to the widgets to control how they are configured and displayed
	 */
	private Properties widgetConfiguration;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default constructor
	 */
	public Parameter() { }
	
	/**
	 * Initialize this Parameter with the given values
	 * @param name The defined descriptive name
	 * @param label The label to display to the user if value is needed
	 * @param type The data type of this parameter
	 * @param collectionType Indicates whether this parameter can have multiple values in a Collection
	 * @param defaultValue The value to fill in if nothing provided by the user
	 * @param widgetConfiguration The widgetConfiguration to be applied to this parameter
	 */
	public Parameter(String name, String label, Class<?> type, 
					 Class<? extends Collection> collectionType, Object defaultValue, Properties widgetConfiguration) {
		super();
		setName(name);
		setLabel(label);
		setType(type);
		setCollectionType(collectionType);
		setDefaultValue(defaultValue);
		setWidgetConfiguration(widgetConfiguration);
	}
	
	/**
	 * Initialize this Parameter with the given values
	 * @param name The defined descriptive name
	 * @param label The label to display to the user if value is needed
	 * @param type The data type of this parameter
	 * @param collectionType Indicates whether this parameter can have multiple values in a Collection
	 * @param defaultValue The value to fill in if nothing provided by the user
	 */
	public Parameter(String name, String label, Class<?> type, 
					 Class<? extends Collection> collectionType, Object defaultValue) {
		this(name, label, type, collectionType, defaultValue, null);
	}
	
	/**
	 * Initialize this Parameter with the given values
	 * 
	 * @param name The defined descriptive name
	 * @param label The label to display to the user if value is needed
	 * @param type The data type of this parameter
	 */
	public Parameter(String name, String label, Class<?> type) {
		this(name, label, type, null, null, null);
	}
	
	/**
	 * Initialize this Parameter with the given values
	 * 
	 * @param name The defined descriptive name
	 * @param label The label to display to the user if value is needed
	 * @param type The data type of this parameter
	 * @param widgetConfiguration The widgetConfiguration to be applied to this parameter
	 */
	public Parameter(String name, String label, Class<?> type, Properties widgetConfiguration) {
		this(name, label, type, null, null, widgetConfiguration);
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Parameter<name="+name+",label="+label);
    	if (collectionType != null) {
    		sb.append(collectionType+"<");
    	}
    	sb.append(",type="+ (type == null ? "null" : type.getName()));
    	if (collectionType != null) {
    		sb.append(">");
    	}
    	sb.append(",defaultValue="+defaultValue+">");
    	if (widgetConfiguration != null) {
    		sb.append("widgetConfiguration=");
    		sb.append(getWidgetConfigurationAsString());
    	}
    	return sb.toString();
    }
    
	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Parameter) {
			Parameter p = (Parameter) obj;
			return StringUtils.equalsIgnoreCase(p.getName(), getName());
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (name == null ? 0 : 31 * name.toUpperCase().hashCode());
	}
	
	/**
	 * Returns the label, if specified, otherwise returns the name.
	 * 
	 * @return
	 */
	public String getLabelOrName() {
		return StringUtils.isNotBlank(label) ? label : name;
	}
    
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * Convenience method to help us retrieve the parameter's default expression.
	 * 
	 * @return	
	 * 			An expression based on the parameter's name.
	 */
	public String getExpression() { 		
		return "${" + getName() + "}";
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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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
	 * @return the collectionType
	 */
	public Class<? extends Collection> getCollectionType() {
		return collectionType;
	}

	/**
	 * @param collectionType the collectionType to set
	 */
	public void setCollectionType(Class<? extends Collection> collectionType) {
		this.collectionType = collectionType;
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
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required the required value to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the widgetConfiguration
	 */
	public Properties getWidgetConfiguration() {
		return widgetConfiguration;
	}

	/**
	 * @param propertyName the widgetConfiguration property to set
	 * @param propertyValue the widgetConfiguration value to set
	 */
	public void addToWidgetConfiguration(String propertyName, String propertyValue) {
		if (widgetConfiguration == null) {
			widgetConfiguration = new Properties();
		}
		widgetConfiguration.setProperty(propertyName, propertyValue);
	}
	
	/**
	 * @return the widgetConfiguration as a pipe delimited list of key value pairs
	 */
	public String getWidgetConfigurationAsString() {
		StringBuilder sb = new StringBuilder();
		if (widgetConfiguration != null) {
			int i = 0;
			for(Object o: widgetConfiguration.keySet()) {
				if(i++ > 0) {
					sb.append("|");
				}
				sb.append(o.toString() + "=" + widgetConfiguration.get(o));
			}
		}
		return sb.toString();
	}

	/**
	 * @param widgetConfiguration the widgetConfiguration to set
	 */
	public void setWidgetConfiguration(Properties widgetConfiguration) {
		this.widgetConfiguration = widgetConfiguration;
	}
}
