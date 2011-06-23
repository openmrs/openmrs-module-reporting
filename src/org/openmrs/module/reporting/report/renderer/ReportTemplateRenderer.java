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
	
	/** 
	 * Returns the template resource
	 */
	public ReportDesignResource getTemplate(ReportDesign design) {
		ReportDesignResource ret = design.getResourceByName("template");
		if (ret == null) {
			ret = design.getResources().iterator().next();
		}
		return ret;
	}
	
	/** 
	 * @see ReportRenderer#getFilename(ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition definition, String argument) {
		ReportDesign d = getDesign(argument);
		String dateStr = DateUtil.formatDate(new Date(), "yyyy-MM-dd-hhmmss");
		return definition.getName() + "_" + dateStr  + "." + getTemplate(d).getExtension();
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
	 * @param design
	 * @return
	 */
	public String getExpressionPrefix(ReportDesign design) {
		return design.getPropertyValue("expressionPrefix", "#");
	}
	
	/**
	 * Returns the string which suffixes a key to replace in the template document
	 * @param design
	 * @return
	 */
	public String getExpressionSuffix(ReportDesign design) {
		return design.getPropertyValue("expressionSuffix", "#");
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
			DataSetRow firstRow = rowIter.next();
			if (!rowIter.hasNext()) {
				data.putAll(getReplacementData(reportData, design, dsName, firstRow));
			}
		}
		
		// Add all parameter values as replacement data
		for (Map.Entry<String, Object> entry : reportData.getContext().getParameterValues().entrySet()) {
			if (!data.containsKey(entry.getKey())) {
				data.put(entry.getKey(), entry.getValue());
			}
			data.put("parameter." + entry.getKey(), entry.getValue());
		}
		
		// Add all design properties as replacement data
		for (Map.Entry<Object, Object> entry : design.getProperties().entrySet()) {
			data.put("property." + entry.getKey(), entry.getValue());
		}
		
		// Add context values as replacement data
		data.put("context.generatedBy", Context.getUserContext().getAuthenticatedUser().getPersonName().toString());
		data.put("context.generationDate", new Date());

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
			Object replacementValue = "";
			if (e.getValue() != null) { 
				if (e.getValue() instanceof Cohort) {
					replacementValue = new Integer(((Cohort) e.getValue()).size());
				} 
				else if (e.getValue() instanceof IndicatorResult) {
					replacementValue = new Double(((IndicatorResult) e.getValue()).getValue().doubleValue());
				}
				else if (e.getValue() instanceof Date) {
					replacementValue = e.getValue();
				}
				else {
					replacementValue = e.getValue().toString();
				}
			}
			data.put(dataSetName + "." + e.getKey().getName(), replacementValue);
			if (reportData.getDataSets().size() == 1) {
				data.put(e.getKey().getName(), replacementValue);
			}
		}
		
		// Add all parameter values as replacement data
		for (Map.Entry<String, Object> entry : reportData.getContext().getParameterValues().entrySet()) {
			if (!data.containsKey(entry.getKey())) {
				data.put(entry.getKey(), entry.getValue());
			}
			data.put("parameter." + entry.getKey(), entry.getValue());
		}
		
		// Add all design properties as replacement data
		for (Map.Entry<Object, Object> entry : design.getProperties().entrySet()) {
			data.put("property." + entry.getKey(), entry.getValue());
		}
		
		data.put("context.generatedBy", Context.getUserContext().getAuthenticatedUser().getPersonName().toString());
		data.put("context.generationDate", new Date());

		return data;
	}
}
