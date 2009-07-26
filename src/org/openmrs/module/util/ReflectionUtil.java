package org.openmrs.module.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	 * @param clazz - The clazz to look at
	 * @param propertyName - The name of the field to return
	 * @return - The field with the given name, or null if not found
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		if (clazz != null) {
			for (Field f : clazz.getDeclaredFields()) {
				if (f.getName().equals(fieldName)) {
					return f;
				}
			}
			return getField(clazz.getSuperclass(), fieldName);
		}
		return null;
	}
	
	/**
	 * Returns the underlying type of the passed field
	 */
	public static Class<?> getFieldType(Field field) {
		Class<?> type = field.getType();
		Class<?> wrapper = getWrapperMap().get(type.getName());
		return (wrapper != null ? wrapper : type);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Object> T getPropertyValue(T object, String property) {
		try {
			String methodName = "get"+StringUtils.capitalize(property);
			Method m = object.getClass().getMethod(methodName, (Class[]) null);
			return (T)m.invoke(object, new Object[] {});
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to access property " + property + " on " + object, e);
		}
	}
}
