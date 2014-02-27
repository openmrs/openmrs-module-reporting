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
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;


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
	public static Object evaluateParameterizable(Parameterizable parameterizable, EvaluationContext context) throws EvaluationException { 
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
				if (genericType instanceof WildcardType) {
					genericType = ((WildcardType)genericType).getUpperBounds()[0];
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
	 * @param obj the Parameterizable to retrieve the value from
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
	
	/**
	 * @return a Map from String->String from the passed paramString
	 */
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
	
	/**
	 * @return a Map, where the entries are the names of each child parameter, and the values are all compatible parent parameters.
	 * The values are organized as a Map where each key is a parent parameter name and each value is a parent parameter Label
	 */
	public static Map<String, Map<String, String>> getAllowedMappings(Parameterizable parent, Parameterizable child) {
		Map<String, Map<String, String>> ret = new HashMap<String, Map<String, String>>();
       	if (child != null) {
			for (Parameter p : child.getParameters()) {
				Map<String, String> allowed  = new HashMap<String, String>();
				for (Parameter parentParam : parent.getParameters()) {
					if (p.getType() == parentParam.getType()) {
						allowed.put(parentParam.getName(), parentParam.getLabelOrName());
					}
				}
				ret.put(p.getName(), allowed);
			}
       	}
       	return ret;
	}
	
	/**
	 * @return a Map, where the entries are the categorizations, and the values are the names and values of the child parameters per category
	 */
	public static Map<String, Map<String, Object>> getCategorizedMappings(Parameterizable parent, Parameterizable child, Map<String, Object> mappings) {
		Map<String, Map<String, Object>> ret = new HashMap<String, Map<String, Object>>();
		
       	Map<String, Object> mappedParams = new HashMap<String, Object>();
       	Map<String, Object> complexParams = new HashMap<String, Object>();
       	Map<String, Object> fixedParams = new HashMap<String, Object>();
       	
       	if (child != null) {
			for (Parameter p : child.getParameters()) {
				Object mappedObjVal = mappings.get(p.getName());
				if (mappedObjVal != null && mappedObjVal instanceof String) {
					String mappedVal = (String) mappedObjVal;
					if (EvaluationUtil.isExpression(mappedVal)) {
						mappedVal = EvaluationUtil.stripExpression(mappedVal);
						if (parent.getParameter(mappedVal) != null) {
							mappedParams.put(p.getName(), mappedVal);
						}
						else {
							complexParams.put(p.getName(), mappedVal);
						}
					}
					else {
						fixedParams.put(p.getName(), mappedVal);
					}
				}
				else {
					fixedParams.put(p.getName(), mappedObjVal);
				}
			}
       	}
		ret.put("mappedParams", mappedParams);
		ret.put("complexParams", complexParams);
		ret.put("fixedParams", fixedParams);
		
		return ret;
	}

	/**
	 * Refreshes this {@link Mapped} with a version of the definition re-fetched from the
	 * {@link DefinitionContext} by uuid.
	 * (Use this method if you have a Mapped definition from one hibernate session that you need to
	 * use in another session.
	 * @param mapped
	 */
	public static <Def extends Definition> void refreshMappedDefinition(Mapped<Def> mapped) {
		try {
			if (mapped.getParameterizable() != null) {
				Def def = mapped.getParameterizable();
				def = (Def) DefinitionContext.getDefinitionByUuid(def.getClass(), def.getUuid());
				mapped.setParameterizable(def);
			}
		}
		catch (Exception e) {
			// Do nothing.  Sometimes this throws a weird hibernate exception, but this method is a hack anyway
		}
	}

	/**
	 * This method takes in a copyFrom Definition that we wish to copy all parameters from,
	 * and adds them all to the passed copyTo Definition, before returning a Mapped containing
	 * the copyFrom Definition and the parameter mappings that relate it to copyTo
	 */
	public static <T extends Definition> Mapped<T> copyAndMap(T copyFrom, Definition copyTo, Map<String, String> renamedParameters) {
		Map<String, Object> mappings = new HashMap<String, Object>();
		for (Parameter p : copyFrom.getParameters()) {
			String paramName = p.getName();
			if (renamedParameters != null && renamedParameters.containsKey(paramName)) {
				paramName = renamedParameters.get(paramName);
			}
			mappings.put(p.getName(), "${" + paramName + "}");
			paramName = EvaluationUtil.parseParameterNameFromExpression(paramName);
			Parameter newParameter = new Parameter();
			newParameter.setName(paramName);
			newParameter.setLabel(p.getLabel());
			newParameter.setType(p.getType());
			newParameter.setCollectionType(p.getCollectionType());
			newParameter.setDefaultValue(p.getDefaultValue());
			newParameter.setWidgetConfiguration(p.getWidgetConfiguration());
			copyTo.addParameter(newParameter);
		}
		return new Mapped<T>(copyFrom, mappings);
	}
}