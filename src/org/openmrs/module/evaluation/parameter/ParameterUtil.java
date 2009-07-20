package org.openmrs.module.evaluation.parameter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

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
    				String name = f.getName();
    				
    				// For a label, the precedence is: labelCode, labelText, name, field name
    				String label = name;
    				if (!StringUtils.isEmpty(ann.labelCode())) {
    					label = Context.getMessageSourceService().getMessage(ann.labelCode());
    				}
    				else if (!StringUtils.isEmpty(ann.labelText())) {
    					label = ann.labelText();
    				}
    				
    				Class<?> paramType = f.getType();
    				Class<? extends Collection<?>> collectionType = null;
    			   	if (Collection.class.isAssignableFrom(f.getType())) {
    			   		Class<?> genericType = getGenericTypeOfCollection(f);
    			   		paramType = genericType;
    			   		collectionType = (Class<? extends Collection<?>>)f.getType();
    			   	}
    				
    				// Populate the default value of the parameter to the value in the passed instance
    				Object defaultVal = null;
    				boolean required = ann.required();
    				
    				if (classInstance != null) {
	    				try {
	    					String getterMethodName = "get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
	    					Method getterMethod = classInstance.getClass().getMethod(getterMethodName, (Class[])null);
	    					defaultVal = getterMethod.invoke(classInstance);
	    				}
	    				catch (Exception e) {
	    					throw new APIException("Error accessing fields in object: " + e);
	    				}
	    				
	    				Parameter p = classInstance.getParameter(name);
	    				if (p != null) {
	    					defaultVal = p.getDefaultValue();
	    					required = required || p.isRequired();
	    				}
    				}

    				// Construct a new Parameter and add it to the return list
    				Parameter p = new Parameter(name, label, paramType, collectionType, defaultVal, required);
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
				Class<?> clazz = p.getCollectionType() != null ? p.getCollectionType() : p.getClazz();
				Method setterMethod = objectToUpdate.getClass().getMethod("set"+baseName, clazz);
				setterMethod.invoke(objectToUpdate, value);
    		}
			catch (Exception e) {
    			throw new APIException("Error setting trying to set annotated parameter <" + p.getName() + "> on class " + 
    									objectToUpdate.getClass() + " with value <" + value + ">");
    		}
    	}
   	}
    
    /**
     * For the passed field, if it is a Collection, it will return the class which represents the generic type of this
     * Collection.  If it is not a Collection, it will return null.  If the type is not a class, it will return null.
     * If it contains more than one Generic Type, it will return the first type found.
     */
    public static Class<?> getGenericTypeOfCollection(Field f) {
    	if (f != null && Collection.class.isAssignableFrom(f.getType())) {
    		try {
				ParameterizedType pt = (ParameterizedType) f.getGenericType();
				Type[] typeArgs = pt.getActualTypeArguments();
				return (Class<?>)typeArgs[0];
			}
			catch (Exception e) {
				log.debug("Unable to retrieve generic type of field: " + f, e);
				// Do nothing
			}
		}
    	return null;
    }
    
    /**
     * Utility method that takes a Collection class and returns a default implementation
     * @param <T>
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Collection> T getNewCollection(Class<T> clazz) {
    	if (Set.class.isAssignableFrom(clazz)) {
    		return (T)new HashSet();
    	}
    	return (T)new ArrayList();
    }
    
    /**
     * Validation method that validates whether the parameter value is compatible with the class
     * @throws ParameterException
     */
    public static void validateParameterValue(Parameter p) throws ParameterException {
		if (p.getDefaultValue() != null &&  p.getClazz() != null) {
			if (!p.getClazz().isAssignableFrom(p.getDefaultValue().getClass())) {
				throw new ParameterException("The class '" + p.getDefaultValue().getClass() + "' of value '" + p.getDefaultValue() + 
											 "' is incompatible with the expected class '" + p.getClazz() + "'");
			}
		}
    }
    
    /**
     * Utility method which takes in a String representation of an Object and returns the Object
     * @param s - The string representation
     * @param clazz - The clazz to return
     * @return - An object of that class
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertStringToObject(String s, Class<? extends T> clazz) {
    	Object parsedObj = OpenmrsUtil.parse(s, clazz);
    	return (T)parsedObj;
    }
}
