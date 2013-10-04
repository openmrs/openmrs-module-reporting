package org.openmrs.module.reporting.common;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A utility class for common reflection methods
 */
public class ReflectionUtil {
	
	protected static Log log = LogFactory.getLog(ReflectionUtil.class);
	
	public static Map<String, Class<?>> getWrapperMap() {
		Map<String, Class<?>> m = new HashMap<String, Class<?>>();
		m.put("int", Integer.class);
		m.put("long", Long.class);
		m.put("double", Double.class);
		m.put("float", Float.class);
		m.put("boolean", Boolean.class);
		m.put("char", Character.class);
		return m;
	}
	
	/**
	 * Returns the field with the given name from the passed class, including it's superclasses
	 * @param type - The type to look at
	 * @param fieldName - The name of the field to return
	 * @return - The field with the given name, or null if not found
	 */
	public static Field getField(Class<?> type, String fieldName) {
		if (type != null) {
			for (Field f : type.getDeclaredFields()) {
				if (f.getName().equals(fieldName)) {
					return f;
				}
			}
			return getField(type.getSuperclass(), fieldName);
		}
		return null;
	}

    public static Class<?> getPropertyType(Class<?> type, String propertyName) {
        if (type != null) {
            for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(type)) {
                if (propertyDescriptor.getName().equals(propertyName)) {
                    return propertyDescriptor.getPropertyType();
                }
            }
        }
        return null;
    }

	/**
	 * Returns true if the passed field is a Collection
	 * @param f the field to check
	 * @return true if the passed field is a Collection
	 */
	public static boolean isCollection(Field f) {
		return Collection.class.isAssignableFrom(f.getType());
	}
	
	/**
	 * Returns the underlying type of the passed field
	 */
	public static Class<?> getFieldType(Field field) {
		Class<?> type = field.getType();
		Class<?> wrapper = getWrapperMap().get(type.getName());
		return (wrapper != null ? wrapper : type);
	}
	
    /**
     * For the passed field, it will attempt to return an array of generic class types
     * If any problem is encountered, it will return null.
     */
    public static Type[] getGenericTypes(Field f) {
    	if (f != null) {
    		try {
				ParameterizedType pt = (ParameterizedType) f.getGenericType();
 				return (Type[])pt.getActualTypeArguments();
			}
			catch (Exception e) {
				log.debug("Unable to retrieve generic type of field: " + f, e);
				// Do nothing
			}
		}
    	return null;
    }
	
    /**
     * Returns the property value with the given property name on the given object.
     * <p>
     * It uses {@link PropertyUtils#getProperty(Object, String)}.
     * 
     * @param object
     * @param property
     * @return the property value with the given property name on the given object
     * @throws IllegalArgumentException if anything goes wrong
     * @should work for string property
     * @should work for boolean property
     * @should work for object property
     * @verifies work for nested property
     */
	public static Object getPropertyValue(Object object, String property) {
    	try {
	        return PropertyUtils.getProperty(object, property);
        }
        catch (Exception e) {
            String message = "Error getting '" + property + "' from " + object;
            if (object != null) {
                message += " (" + object.getClass() + ")";
            }
            throw new IllegalArgumentException(message, e);
        }
	}
	
	/**
	 * Utility method which sets the value of a Field in an Object with the given value
	 */
    public static void setPropertyValue(Object object, String propertyName, Object value) {
    	Field field = getField(object.getClass(), propertyName);
    	setPropertyValue(object, field, value);
   	}
	
	/**
	 * Utility method which sets the value of a Field in an Object with the given value
	 * @param object the object to update.
	 * @param field the field to update.
	 * @param value the value to set on the given object and field.
	 */
    @SuppressWarnings("unchecked")
    public static void setPropertyValue(Object object, Field field, Object value) {
    	
		if (object != null && field != null) {
			boolean getAndAdd = value != null && isCollection(field) && !(value instanceof Collection<?>);
			try {
				String baseName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
				if (getAndAdd) {
					Method getMethod = object.getClass().getMethod("get"+baseName);
					Collection<Object> collection = (Collection<Object>)getMethod.invoke(object);
					if (collection == null) {
						if (List.class.isAssignableFrom(field.getType())) {
							collection = new ArrayList<Object>();
						}
						else {
							collection = new HashSet<Object>();
						}
					}
					collection.add(value);
					value = collection;
				}
				Method setterMethod = object.getClass().getMethod("set"+baseName, field.getType());
				setterMethod.invoke(object, value);
    		}
			catch (Exception e) {
				log.error("Failed to set property value", e);
    			throw new APIException("Error trying to set field <" + field.getName() + "> on " + 
    					object.getClass() + " object with value <" + value + ">", e);
    		}
    	}
   	}
}
