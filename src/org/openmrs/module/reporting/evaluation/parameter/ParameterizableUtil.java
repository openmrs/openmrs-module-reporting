package org.openmrs.module.reporting.evaluation.parameter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;


public class ParameterizableUtil {

	/**
	 * Retrieves a parameterizable with the given uuid and parameterizable class.
	 */
	@SuppressWarnings("unchecked")
	public static Parameterizable getParameterizable(String uuid, Class<? extends Parameterizable> type) { 		
		if (!Definition.class.isAssignableFrom(type)) {
			throw new APIException("Unable to get parameterizable of type " + type);
		}
		Class<? extends Definition> c = (Class<? extends Definition>)type;
		return DefinitionContext.getDefinitionByUuid(c, uuid);	
	}

	/**
	 * Saves the given parameterizable.
	 */
	public static Parameterizable saveParameterizable(Parameterizable parameterizable) { 
		if (parameterizable instanceof Definition) {
			return (Parameterizable) DefinitionContext.saveDefinition((Definition)parameterizable);
		}
		else {
			throw new APIException("Unable to save parameterizable of type " + parameterizable.getClass());
		}
	}

	/**
	 * Evaluates the given parameterizable.
	 */
	public static Object evaluateParameterizable(Parameterizable parameterizable, EvaluationContext context) { 
		Object result = null;
		if (parameterizable != null) {
			if (parameterizable instanceof Definition) {
				result = DefinitionContext.evaluate((Definition)parameterizable, context);
			}
			else { 
				throw new APIException("Unable to evaluate parameterizable of type <" + parameterizable.getClass().getName() + ">");
			}
		}
		return result;
	}
	
	/**
	 * Utility method which will return the underlying Parameterizable type from a class property
	 * that is one of the following supported formats:
	 * 
	 * Mapped<Parameterizable>
	 * Mapped<? extends Parameterizable>
	 * Collection<Mapped<Parameterizable>>
	 * Collection<Mapped<? extends Parameterizable>>
	 * Map<String, Mapped<Parameterizable>>
	 * Map<String, Mapped<? extends Parameterizable>>
	 * 
	 * @param type the class
	 * @param property the property
	 * @return the matching Parameterizable type
	 */
	@SuppressWarnings("unchecked")
    public static Class<? extends Parameterizable> getMappedType(Class<?> type, String property) {
		// Get generic type of the Mapped property, if specified
		Class<? extends Parameterizable> mappedType = null;
		if (StringUtils.isNotEmpty(property)) {
	    	Field f = ReflectionUtil.getField(type, property);
			try {
				Type genericType = null;
				if (Mapped.class.isAssignableFrom(f.getType())) {
					genericType = f.getGenericType();
				}
				else if (Collection.class.isAssignableFrom(f.getType())) {
					ParameterizedType pt = (ParameterizedType) f.getGenericType();
					ParameterizedType mapped = (ParameterizedType)pt.getActualTypeArguments()[0];
					genericType = mapped.getActualTypeArguments()[0];
				}
				else if (Map.class.isAssignableFrom(f.getType())) {
	 				ParameterizedType pt = (ParameterizedType) f.getGenericType();
					ParameterizedType mapped = (ParameterizedType)pt.getActualTypeArguments()[1];
					genericType = mapped.getActualTypeArguments()[0];
				}
				else {
					throw new RuntimeException("Cannot retrieve Mapped type from: " + type.getSimpleName() + "." + property);
				}
				if (genericType instanceof WildcardType) {
					genericType = ((WildcardType) genericType).getUpperBounds()[0];
				}
				if (genericType instanceof ParameterizedType) {
					genericType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
				}
				mappedType = (Class<? extends Parameterizable>) genericType;
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Cannot retrieve Mapped type from: " + type.getSimpleName() + "." + property, e);
			}
		}
		return mappedType;
	}
	
	/**
	 * Utility method which will return the underlying Mapped property of the
	 * Parameterizable from a class property that is one of the following supported formats:
	 * 
	 * Mapped<Parameterizable>
	 * Mapped<? extends Parameterizable>
	 * Collection<Mapped<Parameterizable>>
	 * Collection<Mapped<? extends Parameterizable>>
	 * Map<String, Mapped<Parameterizable>>
	 * Map<String, Mapped<? extends Parameterizable>>
	 * 
	 * @param object the Parameterizable to retrieve the value from
	 * @param property the property
	 * @param collectionKey if a Map or Collection, the key by which to retrieve the value
	 * @return the matching Parameterizable type
	 */
	@SuppressWarnings("unchecked")
    public static Mapped<Parameterizable> getMappedProperty(Parameterizable obj, String property, String collectionKey) {
		if (obj != null) {
			Object propertyValue = ReflectionUtil.getPropertyValue(obj, property);
			Mapped<Parameterizable> mapped = null;
			if (propertyValue != null) {
				try {
		    		if (propertyValue instanceof Mapped) {
		    			mapped = (Mapped<Parameterizable>) propertyValue;
		    		}
		    		else if (StringUtils.isNotEmpty(collectionKey)) {
		    			if (propertyValue instanceof Object[]) {
		    				propertyValue = Arrays.asList((Object[])propertyValue);
		    			}
		    			if (propertyValue instanceof List) {
		    				List l = (List)propertyValue;
		    				int index = Integer.parseInt(collectionKey);
		    				mapped = (Mapped<Parameterizable>) l.get(index);
		    			}
		    			else if (propertyValue instanceof Map) {
		    				Map m = (Map)propertyValue;
		    				mapped = (Mapped<Parameterizable>)  m.get(collectionKey);
		    			}
		    		}
				}
				catch (Exception e) {
					throw new IllegalArgumentException("Mapped Property Editor cannot handle: " + propertyValue);
				}
				return mapped;
			}
		}
		return null;
	}	
	
	public static Map<String, Object> createParameterMappings(String paramString) {
		Map<String, Object> m = new HashMap<String, Object>();
		if (paramString != null) {
			try {
				String[] split = paramString.split(",");
				for (int i=0; i<split.length; i++) {
					String[] keyVal = split[i].split("=");
					if (keyVal.length > 1) { // sanity check
						m.put(keyVal[0], keyVal[1]);
					}
				}
			}
			catch (Exception e) {
				throw new RuntimeException("Error while setting parameter mappings from String", e);
			}
		}
		return m;
	}
}