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
package org.openmrs.module.reporting.report.renderer;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Abstract super-class for all Renderer classes that render utilizing a ReportTemplate
 */
public abstract class ReportTemplateRenderer extends ReportDesignRenderer {
	
	//***** CONSTANT REPORT TEMPLATE PROPERTIES THAT ARE AVAILABLE
	public static final String CONTEXT_PREFIX = "context";
	public static final String ROW_CONTEXT_PREFIX = "rowContext";
	public static final String PARAMETER_PREFIX = "parameter";
	public static final String PROPERTY_PREFIX = "property";
	public static final String SEPARATOR = ".";
	public static final String INDEX = "index";
	public static final String LABEL = "label";
	
	/** 
	 * Returns the template resource
	 */
	public ReportDesignResource getTemplate(ReportDesign design) {
		ReportDesignResource ret = design.getResourceByName("template");
		if (ret == null && design.getResources().iterator().hasNext()) {
			ret = design.getResources().iterator().next();
		}
		return ret;
	}
	
	/** 
	 * @see ReportRenderer#getFilename(ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition definition, String argument) {
		ReportDesign d = getDesign(argument);
		ReportDesignResource rds = getTemplate(d);
		if ( rds == null ) return definition.getName() + ".xls";  
		String dateStr = DateUtil.formatDate(new Date(), "yyyy-MM-dd-hhmmss");
		return definition.getName() + "_" + dateStr  + "." + rds.getExtension();
	}
	
	/** 
	 * @see ReportRenderer#getRenderedContentType(ReportDefinition, String)
	 */
	public String getRenderedContentType(ReportDefinition definition, String argument) {
		ReportDesign d = getDesign(argument);
		return getTemplate(d).getContentType();
	}
	
	/**
	 * Returns the string which prefixes a key to replace in the template document
	 * @return
	 */
	public String getExpressionPrefix() {
		return "#";
	}
	
	/**
	 * Returns the string which suffixes a key to replace in the template document
	 * @return
	 */
	public String getExpressionSuffix() {
		return "#";
	}
	
	/**
	 * Constructs a Map from String to Object of all data than can be used as replacements within an entire template
	 * This includes all parameters, design properties, context values, and data for any datasets that have only a single row
	 * @return Map from String to Object of all data than can be used as replacements in the template
	 */
	public Map<String, Object> getBaseReplacementData(ReportData reportData, ReportDesign design) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		// Add data set values if there is only a single row in the dataset
		for (String dsName : reportData.getDataSets().keySet()) {
			DataSet ds = reportData.getDataSets().get(dsName);
			Iterator<DataSetRow> rowIter = ds.iterator();
			if (rowIter.hasNext()) {
				DataSetRow firstRow = rowIter.next();
				if (!rowIter.hasNext()) {
					data.putAll(getReplacementData(reportData, design, dsName, firstRow));
				}
			}
		}
		
		// Add all parameter values as replacement data
		for (Map.Entry<String, Object> entry : reportData.getContext().getParameterValues().entrySet()) {
			if (!data.containsKey(entry.getKey())) {
				data.put(entry.getKey(), entry.getValue());
			}
			data.put(PARAMETER_PREFIX + SEPARATOR + entry.getKey(), entry.getValue());
		}
		
		// Add all design properties as replacement data
		for (Map.Entry<Object, Object> entry : design.getProperties().entrySet()) {
			data.put(PROPERTY_PREFIX + SEPARATOR  + entry.getKey(), entry.getValue());
		}
		
		// Add all context values as replacement data
		for (Map.Entry<String, Object> entry : reportData.getContext().getContextValues().entrySet()) {
			data.put(CONTEXT_PREFIX + SEPARATOR  + entry.getKey(), entry.getValue());
		}

		return data;
	}
	
	/**
	 * Constructs a Map from String to Object of all data than can be used as replacements for the given data set row
	 * @param reportData
	 * @param template
	 * @return Map from String to Object of all data than can be used as replacements in the template
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getReplacementData(ReportData reportData, ReportDesign design, String dataSetName, DataSetRow row) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		// Add row to replacement data
		for (Object entry : row.getColumnValues().entrySet()) {
			
			Map.Entry<DataSetColumn, Object> e = (Map.Entry<DataSetColumn, Object>) entry;
			String baseKey = dataSetName + SEPARATOR + e.getKey().getName();
			
			Object replacementValue = getReplacementValue(e.getValue());
			data.put(baseKey, replacementValue);
			String columnLabel = Context.getMessageSourceService().getMessage(e.getKey().getLabel());
			data.put(baseKey + SEPARATOR + LABEL, columnLabel);

			if (reportData.getDataSets().size() == 1) {
				data.put(e.getKey().getName(), replacementValue);
				data.put(e.getKey().getName() + SEPARATOR + LABEL, columnLabel);
			}
		}
		
		// Add all parameter values as replacement data
		for (Map.Entry<String, Object> entry : reportData.getContext().getParameterValues().entrySet()) {
			if (!data.containsKey(entry.getKey())) {
				data.put(entry.getKey(), entry.getValue());
			}
			data.put(PARAMETER_PREFIX + SEPARATOR + entry.getKey(), entry.getValue());
		}
		
		// Add all design properties as replacement data
		for (Map.Entry<Object, Object> entry : design.getProperties().entrySet()) {
			data.put(PROPERTY_PREFIX + SEPARATOR  + entry.getKey(), entry.getValue());
		}
		
		// Add all context values as replacement data
		for (Map.Entry<String, Object> entry : reportData.getContext().getContextValues().entrySet()) {
			data.put(CONTEXT_PREFIX + SEPARATOR  + entry.getKey(), entry.getValue());
		}

		return data;
	}
	
	/**
	 * @return the value for the report template replacement given the initial value
	 */
	public Object getReplacementValue(Object initialValue) {
		Object replacementValue = "";
		if (initialValue != null) { 
			if (initialValue instanceof Cohort) {
				replacementValue = new Integer(((Cohort) initialValue).size());
			} 
			else if (initialValue instanceof IndicatorResult) {
				replacementValue = new Double(((IndicatorResult) initialValue).getValue().doubleValue());
			}
			else {
				replacementValue = initialValue;
			}
		}
		return replacementValue;
	}
}
