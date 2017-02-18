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
package org.openmrs.module.reporting.definition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.util.HandlerUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods useful for Definitions
 */
public class DefinitionUtil {
	
	private static Log log = LogFactory.getLog(DefinitionUtil.class);

	public static String format(Definition d) {
		StringBuilder sb = new StringBuilder();
		sb.append(d.getClass().getSimpleName()).append("[");
		if (ObjectUtil.notNull(d.getName())) {
			sb.append(d.getName());
		}
		else {
			StringBuilder configStr = new StringBuilder();
			try {
				for (Property p : getConfigurationProperties(d)) {
					if (ObjectUtil.notNull(p.getValue())) {
						configStr.append(configStr.length() > 0 ? "," : "").append(p.getDisplayName()).append("=").append(ObjectUtil.format(p.getValue()));
					}
				}
				sb.append(configStr);
			}
			catch (Exception e) {
				sb.append("?");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Utility method which takes in an Object and returns a List of {@link Property}s
	 * based on the annotated {@link ConfigurationProperty} fields within its class or superclasses.
	 * @param classInstance - The instance from which to retrieve Param fields
	 * @return - A List of {@link Property}s based on the annotations in the passed instance class
	 */
	public static List<Property> getConfigurationProperties(Definition classInstance) {
		return getConfigurationProperties(classInstance.getClass(), classInstance);
	}
	
	/**
	 * Utility method which takes in an Object class and instance 
	 * and returns a List of {@link Property}s based on the annotated {@link ConfigurationProperty}
	 * fields within the classToCheck or its superclasses.
	 * This is private as it exists only to support recursion in the above class.
	 * @param classToCheck - The class to look at for annotated fields.
	 * @param classInstance - The instance to look at for default values.
	 * @return - A List of {@link Property}s based on the annotations in the passed classes
	 */
	@SuppressWarnings("rawtypes")
    private static List<Property> getConfigurationProperties(Class<?> classToCheck, Object classInstance) {
    	
    	List<Property> ret = new ArrayList<Property>();
    	
    	if (classToCheck != null) {

			// If this class extends another class, then inspect all inherited field values as well
	    	Class superclass = classToCheck.getSuperclass();
	    	if (superclass != null) {
	    		ret.addAll(getConfigurationProperties(superclass, classInstance));
	    	}
    		
    		String prefix = null;
    		Localized l = classToCheck.getAnnotation(Localized.class);
    		if (l != null) {
    			prefix = l.value() + ".";
    		}

    		// Iterate across all of the declared fields in the passed class
	    	for (Field f : classToCheck.getDeclaredFields()) {
    			ConfigurationProperty ann = f.getAnnotation(ConfigurationProperty.class);
    			
    			// If it is annotated to accept parameters, then retrieve values for Parameter
    			if (ann != null) {
    				Object value = ReflectionUtil.getPropertyValue(classInstance, f.getName());
    				
    				// By default, show the property name, within a blank group, for each field
    				String displayName = f.getName();
    				String groupName = "";
    				
    				// If the ConfigurationProperty specifies the property text, use it
    				if (ObjectUtil.notNull(ann.value())) {
    					displayName = MessageUtil.translate(ann.value());
    				}
    				else {
    					// Otherwise, try to imply the text from convention
    					if (prefix != null) {
        					displayName = MessageUtil.translate(prefix + f.getName(), displayName);
    					}
    				}
    				
    				// If the group is specified, then set the groupName by default to it and try to translate
					if (ObjectUtil.notNull(ann.group())) {
						groupName = ann.group();
						if (prefix != null) {
							groupName = MessageUtil.translate(prefix + ann.group(), groupName);
						}
						if (ObjectUtil.isNull(groupName)) {
							groupName = MessageUtil.translate(ann.group(), groupName);
						}
					}

    				Property p = new Property(f, value, ann.required(), displayName, groupName, ann.displayFormat(), ann.displayAttributes());
    				ret.add(p);
    			}
	    	}
    	}
    	return ret;
   	}
	
	/**
	 * Utility method which takes in a Definition instance and returns a
	 * new instance with identical properties for any that are annotated as {@link ConfigurationProperty}
	 * @param instanceToClone - The Definition to clone
	 * @return - A new instance with all Parameter-based properties cloned
	 */
	@SuppressWarnings("unchecked")
    public static <T extends Definition> T clone(T instanceToClone) {
		T newInstance = null;
		if (instanceToClone != null) {
			try {
				newInstance = (T) instanceToClone.getClass().newInstance();
				newInstance.setId(instanceToClone.getId());
				newInstance.setUuid(instanceToClone.getUuid());
				newInstance.setName(instanceToClone.getName());
				newInstance.setDescription(instanceToClone.getDescription());
				newInstance.setCreator(instanceToClone.getCreator());
				newInstance.setDateCreated(instanceToClone.getDateCreated());
				newInstance.setChangedBy(instanceToClone.getChangedBy());
				newInstance.setDateChanged(instanceToClone.getDateChanged());
				newInstance.setRetired(instanceToClone.isRetired());
				newInstance.setRetiredBy(instanceToClone.getRetiredBy());
				newInstance.setDateRetired(instanceToClone.getDateRetired());
				newInstance.setRetireReason(instanceToClone.getRetireReason());
				
				for (Property p : getConfigurationProperties(instanceToClone)) {
					Object toCopy = ReflectionUtil.getPropertyValue(instanceToClone, p.getField().getName());
					if (toCopy instanceof Definition) {
						toCopy = DefinitionUtil.clone((Definition)toCopy);
					}
					ReflectionUtil.setPropertyValue(newInstance, p.getField(), toCopy);
				}
				
				for (Parameter p : instanceToClone.getParameters()) {
					newInstance.addParameter(p);
				}
			}
			catch (Exception e) {
				throw new APIException("Error which trying to clone a " + instanceToClone.getClass(), e);
			}
		}
		return newInstance;
	}
	
	/**
	 * @return the DefinitionEvaluator that is preferred for the passed definition
	 * @throws EvaluationException if no appropriate evaluator is found
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Definition> DefinitionEvaluator<T> getPreferredEvaluator(T definition) throws EvaluationException {
		DefinitionEvaluator<T> evaluator = HandlerUtil.getPreferredHandler(DefinitionEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new EvaluationException("No Evaluator found for (" + definition.getClass() + ") " + definition.getName());
		}
		return evaluator;
	}

	/**
	 * Utility method which takes in a Definition instance, clones it, and populates any properties
	 * from parameters in the passed EvaluationContext as appropriate
	 * @return - A new instance with all Parameter-based properties cloned and properties populated
	 */
    public static <T extends Definition> T cloneDefinitionWithContext(T definition, EvaluationContext context) {
		T clonedDefinition = DefinitionUtil.clone(definition);
		for (Parameter p : clonedDefinition.getParameters()) {
			Object value = p.getDefaultValue();
			if (context != null && context.containsParameter(p.getName())) {
				value = context.getParameterValue(p.getName());
			}
			ReflectionUtil.setPropertyValue(clonedDefinition, p.getName(), value);
		}
		return clonedDefinition;
	}
	
    /**
     * Generic method which takes in a property name, and returns either a Parameter value from the 
     * EvaluationContext with this name, if it exists, or the configured property value otherwise
     */
    @SuppressWarnings("unchecked")
    public static <P> P getConfiguredProperty(Definition d, String name, EvaluationContext context, Class<P> type) {
		P p = (P)context.getParameterValue(name);
		if (p != null) {
			 return p;
		}
		return (P)ReflectionUtil.getPropertyValue(d, name);
    }

	public static List<Location> getAllLocationsAndChildLocations(List<Location> locations) {
		if (locations == null) {
			return null;
		}

		final List<Location> result = new ArrayList<Location>();
		for (Location location: locations) {
			addLocations(result, location);
		}
		return result;
	}

	private static void addLocations(List<Location> list, Location location) {
		if (location.getChildLocations() != null && !location.getChildLocations().isEmpty()) {
			for (Location sublocation: location.getChildLocations()) {
				addLocations(list, sublocation);
			}
		}
		list.add(location);
	}
}
