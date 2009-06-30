package org.openmrs.module.evaluation.parameter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

/**
 * A utility class for working with Parameters and related classes.
 */
public class ParameterUtil {
	
	private static Log log = LogFactory.getLog(ParameterUtil.class);
	
	/**
	 * Utility method which takes in a Parameterizable instance 
	 * and returns a List of Parameters based on the annotated {@link Param}
	 * fields within its class or superclasses
	 * @param classInstance - The instance from which to retrieve Param fields
	 * @return - A List of Parameters based on the annotations in the passed instance class
	 */
	public static List<Parameter> getAnnotatedParameters(Parameterizable classInstance) {
		return getAnnotatedParameters(classInstance.getClass(), classInstance);
	}
	
	/**
	 * Utility method which takes in a Class and a Parameterizable instance 
	 * and returns a List of Parameters based on the annotated {@link Param}
	 * fields within the classToCheck or its superclasses.
	 * This is private as it exists only to support recursion in the above class.
	 * Made public again because of a dependency on BaseDataSetDefinition.
	 * @param classToCheck - The class to look at for annotated fields.
	 * @param classInstance - The instance to look at for default values.
	 * @return - A List of Parameters based on the annotations in the passed classes
	 */
	@SuppressWarnings("unchecked")
    public static List<Parameter> getAnnotatedParameters(Class<?> classToCheck, Parameterizable classInstance) {
    	
    	List<Parameter> ret = new ArrayList<Parameter>();
    	
    	if (classToCheck != null) {
    		log.debug("In class: " + classToCheck.getName());
    		
    		// Iterate across all of the declared fields in the passed class
	    	for (Field f : classToCheck.getDeclaredFields()) {
    			Param ann = f.getAnnotation(Param.class);
    			
    			// If it is annotated to accept parameters, then retrieve values for Parameter
    			if (ann != null) {
    				
    				// If a name attribute is included, use it, otherwise use the field name
    				String name = (StringUtils.isEmpty(ann.name()) ? f.getName() : ann.name());
    				
    				// For a label, the precedence is: labelCode, labelText, name, field name
    				String label = name;
    				if (!StringUtils.isEmpty(ann.labelCode())) {
    					label = Context.getMessageSourceService().getMessage(ann.labelCode());
    				}
    				else if (!StringUtils.isEmpty(ann.labelText())) {
    					label = ann.labelText();
    				}
    				
    				// Populate the default value of the parameter to the value in the passed instance
    				Object defaultVal = null;
    				if (classInstance != null) {
	    				try {
	    					String getterMethodName = "get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
	    					Method getterMethod = classInstance.getClass().getMethod(getterMethodName, (Class[])null);
	    					defaultVal = getterMethod.invoke(classInstance);
	    				}
	    				catch (Exception e) {
	    					throw new APIException("Error accessing fields in object: " + e);
	    				}
    				}
    				
    				// Construct a new Parameter and add it to the return list
    				Parameter p = new Parameter(name, label, f.getType(), defaultVal, ann.required(), false);
    				log.debug("Adding parameter: " + p);
    				ret.add(p);
    			}
	    	}
	    	
			// If this class extends another class, then inspect all inherited field values as well
	    	Class superclass = classToCheck.getSuperclass();
	    	if (superclass != null) {
	    		log.debug("Checking superclass: " + superclass);
	    		ret.addAll(getAnnotatedParameters(superclass, classInstance));
	    	}
    	}
    	return ret;
   	}
	
	/**
	 * Utility method which takes in a Parameterizable instance and returns a
	 * new instance with identical properties for any that are annotated as  {@link Param}
	 * @param classToCheck - The class to look at for annotated fields.
	 * @param classInstance - The instance to look at for default values.
	 * @return - A new instance with all Parameter-based properties cloned
	 */
	@SuppressWarnings("unchecked")
    public static <T extends Parameterizable> T cloneParameterizable(T instanceToClone) {
		T newInstance = null;
		if (instanceToClone != null) {
			try {
				newInstance = (T) instanceToClone.getClass().newInstance();
				
				// First, clone all fields annotated as Param
				cloneAnnotatedFields(instanceToClone.getClass(), instanceToClone, newInstance);
				
				// Next, copy all configured parameters across
				for (Parameter p : instanceToClone.getParameters()) {
					newInstance.addParameter(p);
				}
			}
			catch (Exception e) {
				throw new APIException("Error which trying to clone parameterizable.", e);
			}
		}
		return newInstance;
	}
	
	/**
	 * Utility method which takes in a Parameterizable instance and returns a
	 * new instance with identical properties for any that are annotated as  {@link Param}
	 * This method is private as it exists to support recursion of the above method
	 * @param classToCheck - The class to look at for annotated fields.
	 * @param classInstance - The instance to look at for default values.
	 * @return - A new instance with all Parameter-based properties cloned
	 */
    private static <T extends Parameterizable> void cloneAnnotatedFields(Class<? extends Parameterizable> clazz, 
                                                                         T oldInstance,
                                                                         T newInstance) {
		// First set any Properties that are annotated as Param
    	for (Field f : clazz.getDeclaredFields()) {
			Param ann = f.getAnnotation(Param.class);
			if (ann != null) {
				if (oldInstance != null) {
    				try {
    					String baseName = f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
    					Method getterMethod = oldInstance.getClass().getMethod("get"+baseName, (Class[])null);
    					Class<?> methodType = getterMethod.getReturnType();
    					Object valToCopy = getterMethod.invoke(oldInstance);
    					Method setterMethod = clazz.getMethod("set"+baseName, methodType);
    					setterMethod.invoke(newInstance, valToCopy);
    				}
    				catch (Exception e) {
    					throw new APIException("Error accessing fields in object: " + e);
    				}
				}
			}
    	}
	}
    
	/**
	 * Utility method which sets the value of a Field in a Parameterizable with the
	 * value from the passed Parameter
	 */
    public static <T extends Parameterizable> void setAnnotatedFieldFromParameter(Parameterizable objectToUpdate, Parameter p, Object value) {
		if (objectToUpdate != null && p != null) {
			try {
				String baseName = p.getName().substring(0, 1).toUpperCase() + p.getName().substring(1);
				Method setterMethod = objectToUpdate.getClass().getMethod("set"+baseName, p.getClazz());
				setterMethod.invoke(objectToUpdate, value);
    		}
			catch (Exception e) {
    			throw new APIException("Error setting trying to set annotated parameter <" + p.getName() + "> on class " + 
    									objectToUpdate.getClass() + " with value <" + value + "> of class " + value.getClass());
    		}
    	}
   	}
}
