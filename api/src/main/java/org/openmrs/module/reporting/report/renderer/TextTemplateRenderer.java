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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;

/**
 * Report Renderer implementation that supports rendering of a text template
 */
@Handler
@Localized("reporting.TextTemplateRenderer")
public class TextTemplateRenderer extends ReportTemplateRenderer {
	
	public static String SCRIPT_ENGINE_NAME = "ScriptEngineName";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public TextTemplateRenderer() {
		super();
	}
	
	/**
	 * Renders a report template by doing simple variable replacement.
	 * 
	 * @param reportData
	 * @param outputStream
	 * @param reportDesign
	 * @param reportDesignResource
	 * @param writter
	 * @throws IOException
	 * @throws RenderingException
	 */
	public void render(ReportData reportData, OutputStream outputStream, ReportDesign reportDesign,
	                   ReportDesignResource reportDesignResource, Writer writter) throws IOException, RenderingException {
		Map<String, Object> replacements = new HashMap<String, Object>();
		
		for (String dsName : reportData.getDataSets().keySet()) {
			DataSet ds = reportData.getDataSets().get(dsName);
			int num = 0;
			for (DataSetRow row : ds) {
				if (num++ > 0) {
					throw new RuntimeException("Currently only datasets with one row are supported.");
				}
				replacements.putAll(getReplacementData(reportData, reportDesign, dsName, row));
			}
		}
		
		String prefix = getExpressionPrefix(reportDesign);
		String suffix = getExpressionSuffix(reportDesign);
		
		String templateContents = new String(reportDesignResource.getContents(), "UTF-8");
		writter.write(EvaluationUtil.evaluateExpression(templateContents, replacements, prefix, suffix).toString());
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
			
			String scriptEngineName = reportDesign.getPropertyValue(SCRIPT_ENGINE_NAME, null);
			if (scriptEngineName == null) {
				//Just do simple variable replacement
				render(reportData, out, reportDesign, reportDesignResource, pw);
			} else {
				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine scriptEngine = manager.getEngineByName(scriptEngineName);
				
				scriptEngine.put("reportData", reportData);
				scriptEngine.put("reportDesign", reportDesign);
				scriptEngine.put("util", new ObjectUtil());
				
				String templateContents = new String(reportDesignResource.getContents(), "UTF-8");
				Object output = scriptEngine.eval(templateContents);
				
				pw.write(output.toString());
			}
		}
		catch (Throwable e) {
			throw new RenderingException("Unable to render results due to: " + e, e);
		}
		finally {
			IOUtils.closeQuietly(pw);
		}
	}
}
