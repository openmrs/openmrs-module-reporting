package org.openmrs.module.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;


public class ParameterizableUtil {

	/**
	 * Retrieves a parameterizable with the given uuid and parameterizable class.
	 * 
	 * @param uuid
	 * @return
	 */
	public static Parameterizable getParameterizable(String uuid, Class<? extends Parameterizable> parameterizableClass) { 
		
		if (DataSetDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(DataSetDefinitionService.class).getDataSetDefinitionByUuid(uuid);			
		} 
		else if (CohortDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(CohortDefinitionService.class).getCohortDefinitionByUuid(uuid);
		}
		else if (ReportDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);						
		}
		else if (Indicator.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);	
		}
		else { 
			throw new APIException("Unable to save parameterizable type " + parameterizableClass);
		}		
	}

	

	/**
	 * Saves the given parameterizable.
	 * 
	 * @param parameterizable
	 * @return
	 */
	public static Parameterizable saveParameterizable(Parameterizable parameterizable) { 

		if (DataSetDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(DataSetDefinitionService.class).saveDataSetDefinition((DataSetDefinition)parameterizable);			
		} 
		else if (CohortDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(CohortDefinitionService.class).saveCohortDefinition((CohortDefinition)parameterizable);
		}
		else if (ReportDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(ReportService.class).saveReportDefinition((ReportDefinition)parameterizable);						
		}
		else if (Indicator.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(IndicatorService.class).saveIndicator((Indicator)parameterizable);	
		}
		else { 
			throw new APIException("Unable to save parameterizable type " + parameterizable.getClass());
		}
		//return parameterizable;
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
	
}