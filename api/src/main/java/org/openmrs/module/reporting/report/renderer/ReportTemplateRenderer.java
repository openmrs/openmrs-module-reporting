/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetMetaData;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Abstract super-class for all Renderer classes that render utilizing a ReportTemplate
 */
public abstract class ReportTemplateRenderer extends ReportDesignRenderer {

    private Log log = LogFactory.getLog(this.getClass());
	
	//***** CONSTANT REPORT TEMPLATE PROPERTIES THAT ARE AVAILABLE
	public static final String CONTEXT_PREFIX = "context";
	public static final String ROW_CONTEXT_PREFIX = "rowContext";
	public static final String PARAMETER_PREFIX = "parameter";
	public static final String PROPERTY_PREFIX = "property";
	public static final String SEPARATOR = ".";
	public static final String INDEX = "index";
	public static final String LABEL = "label";

	public static final String COLUMN_TRANSLATION_PREFIX_DESIGN_PROPERTY = "columnTranslationPrefix";
    public static final String COLUMN_TRANSLATION_LOCALE_DESIGN_PROPERTY = "columnTranslationLocale";
	
	/** 
	 * Returns the template resource
	 */
	public ReportDesignResource getTemplate(ReportDesign design) {
		ReportDesignResource ret = design.getResourceByName("template");
		if (ret == null) {
			if (design.getResources().size() > 0) {
				ret = design.getResources().iterator().next();
			}
		}
		return ret;
	}
	
	/** 
	 * @see ReportRenderer#getFilename(org.openmrs.module.reporting.report.ReportRequest)
	 */
	public String getFilename(ReportRequest request) {
        String argument = request.getRenderingMode().getArgument();
        String fileName = getFilenameBase(request);
		ReportDesign d = getDesign(argument);
		ReportDesignResource template = getTemplate(d);
		if (template != null) {
			return fileName + "." + template.getExtension();
		}
		return fileName;
	}
	
	/** 
	 * @see ReportRenderer#getRenderedContentType(org.openmrs.module.reporting.report.ReportRequest)
	 */
	public String getRenderedContentType(ReportRequest request) {
		ReportDesign d = getDesign(request.getRenderingMode().getArgument());
		ReportDesignResource template = getTemplate(d);
		if (template != null) {
			return template.getContentType();
		}
		return "";
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

		String translationPrefix = getTranslationPrefix(design);
        Locale translationLocale = getTranslationLocale(design);
		
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

			// Add column labels to replacement data, supporting translations if configured
			DataSetMetaData metaData = ds.getMetaData();
			for (DataSetColumn column : metaData.getColumns()) {

                String columnLabel = ObjectUtil.nvlStr(column.getLabel(), column.getName());
                String key = translationPrefix + dsName + SEPARATOR + columnLabel;
                String value = MessageUtil.translate(key, columnLabel, translationLocale);
                data.put(dsName + SEPARATOR + column.getName() + SEPARATOR + LABEL, value);

                if (reportData.getDataSets().size() == 1) {
                    key = translationPrefix + columnLabel;
                    value = MessageUtil.translate(key, columnLabel, translationLocale);
                    data.put(column.getName() + SEPARATOR + LABEL, value);
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
	 * @param design
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
					IndicatorResult ir = (IndicatorResult) initialValue;
					replacementValue = ir.getValue();
				}
				else {
					replacementValue = initialValue;
				}
		}
		return replacementValue;
	}

    /**
     * @return the message code prefix to use for translation columns.
     * For example, if a column had a label of "gender" and a translation prefix of "myreports.reportcolumns.", then
     * it would look for a message code named "myreports.reportcolumns.gender"
     */
	protected String getTranslationPrefix(ReportDesign design) {
        return design.getPropertyValue(COLUMN_TRANSLATION_PREFIX_DESIGN_PROPERTY, "");
    }

    /**
     * @return the locale to use for translating columns.
     * This will first look at a design property named columnTranslationLocale
     */
	protected Locale getTranslationLocale(ReportDesign design) {
        Locale translationLocale = null;
        String translationLocaleProperty = design.getPropertyValue(COLUMN_TRANSLATION_LOCALE_DESIGN_PROPERTY, null);
        if (ObjectUtil.isNull(translationLocaleProperty)) {
            translationLocale = ReportingConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE();
        }
        else {
            try {
                translationLocale = new Locale(translationLocaleProperty);
            }
            catch (Exception e) {
                log.warn("Unable to create locale using design property: " + translationLocaleProperty);
            }
        }
        if (translationLocale == null) {
            translationLocale = Context.getLocale();
        }
        return translationLocale;
    }
}
