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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.renderer.template.TemplateEngine;
import org.openmrs.module.reporting.report.renderer.template.TemplateEngineManager;

/**
 * Report Renderer implementation that supports rendering of a text template
 */
@Handler
@Localized("reporting.TextTemplateRenderer")
public class TextTemplateRenderer extends ReportTemplateRenderer {
	
	public static final String TEMPLATE_TYPE = "templateType";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public TextTemplateRenderer() {
		super();
	}
	
	/**
	 * @see ReportTemplateRenderer#getBaseReplacementDataReportData, ReportDesign)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getBaseReplacementData(ReportData reportData, ReportDesign design) {
		// Populate the replacement data with all core values, and any data sets with only one row
		Map<String, Object> data = super.getBaseReplacementData(reportData, design);
		
		// Now go through and add data sets and add rows by index to replacement data as well
		for (String dataSetName : reportData.getDataSets().keySet()) {
			DataSet ds = reportData.getDataSets().get(dataSetName);
			int rowNum = 0;
			for (DataSetRow row : ds) {
				for (Object entry : row.getColumnValues().entrySet()) {
					rowNum++;
					Map.Entry<DataSetColumn, Object> e = (Map.Entry<DataSetColumn, Object>) entry;
					String baseKey = dataSetName + SEPARATOR + e.getKey().getName() + SEPARATOR + rowNum;
					Object replacementValue = getReplacementValue(e.getValue());
					data.put(baseKey, replacementValue);
					String columnLabel = Context.getMessageSourceService().getMessage(e.getKey().getLabel());
					data.put(baseKey + SEPARATOR + LABEL, columnLabel);
					if (reportData.getDataSets().size() == 1) {
						data.put(e.getKey().getName() + SEPARATOR + rowNum, replacementValue);
						data.put(e.getKey().getName() + SEPARATOR + rowNum + SEPARATOR + LABEL, columnLabel);
					}
				}				
			}
		}
		return data;
	}
	
	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		
		log.debug("Attempting to render report with TextTemplateRenderer");
		
		Writer pw = new OutputStreamWriter(out, "UTF-8");
		
		try {
			ReportDesign reportDesign = getDesign(argument);
			ReportDesignResource reportDesignResource = getTemplate(reportDesign);
			String templateContents = new String(reportDesignResource.getContents(), "UTF-8");
			Map<String, Object> replacements = getBaseReplacementData(reportData, reportDesign);
	
			// First, run the template through any engine that is specified
			String templateEngineName = reportDesign.getPropertyValue(TEMPLATE_TYPE, null);
			TemplateEngine engine = TemplateEngineManager.getTemplateEngineByName(templateEngineName);
			if (engine != null) {
				Map<String, Object> bindings = new HashMap<String, Object>();
				bindings.put("reportData", reportData);
				bindings.put("reportDesign", reportDesign);
				bindings.put("data", replacements);
				bindings.put("util", new ObjectUtil());
				bindings.put("dateUtil", new DateUtil());
				bindings.put("msg", new MessageUtil());
				templateContents = engine.evaluate(templateContents, bindings);
			}
			
			// Now, apply any direct variable replacements that might be applicable
			String prefix = getExpressionPrefix(reportDesign);
			String suffix = getExpressionSuffix(reportDesign);
			templateContents = EvaluationUtil.evaluateExpression(templateContents, replacements, prefix, suffix).toString();
			
			pw.write(templateContents.toString());
		}
		catch (Throwable e) {
			throw new RenderingException("Unable to render results due to: " + e, e);
		}
		finally {
			IOUtils.closeQuietly(pw);
		}
	}
}
