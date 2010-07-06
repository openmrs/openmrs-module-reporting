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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * Provides utility methods useful for Definitions
 */
public class DefinitionUtil {
	
	private static Log log = LogFactory.getLog(DefinitionUtil.class);
	
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
	@SuppressWarnings("unchecked")
    private static List<Property> getConfigurationProperties(Class<?> classToCheck, Object classInstance) {
    	
    	List<Property> ret = new ArrayList<Property>();
    	
    	if (classToCheck != null) {
    		log.debug("In class: " + classToCheck.getName());
    		
			// If this class extends another class, then inspect all inherited field values as well
	    	Class superclass = classToCheck.getSuperclass();
	    	if (superclass != null) {
	    		log.debug("Checking superclass: " + superclass);
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

    				Property p = new Property(f, value, ann.required(), displayName, groupName);
    				log.debug("Adding: " + p);
    				ret.add(p);
    			}
	    	}
    	}
    	return ret;
   	}
	
	/**
	 * Utility method which takes in a Definition instance and returns a
	 * new instance with identical properties for any that are annotated as {@link ConfigurationProperty}
	 * @param classToCheck - The class to look at for annotated fields.
	 * @param classInstance - The instance to look at for default values.
	 * @return - A new instance with all Parameter-based properties cloned
	 */
	@SuppressWarnings("unchecked")
    public static <T extends Definition> T clone(T instanceToClone) {
		T newInstance = null;
		if (instanceToClone != null) {
			try {
				newInstance = (T) instanceToClone.getClass().newInstance();
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
}
