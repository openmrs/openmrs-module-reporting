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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;

/**
 * Report Renderer implementation that supports rendering to an Excel template
 */
@Handler
@Localized("reporting.TextTemplateRenderer")
public class TextTemplateRenderer extends ReportTemplateRenderer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public TextTemplateRenderer() {
		super();
	}

	/** 
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		
		log.debug("Attempting to render report with TextTemplateRenderer");

		PrintWriter pw = null;
		try {
			ReportDesign design = getDesign(argument);
			ReportDesignResource r = getTemplate(design);
			
			Map<String, Object> replacements = new HashMap<String, Object>();
			
			for (String dsName : reportData.getDataSets().keySet()) {
				DataSet ds = reportData.getDataSets().get(dsName);
				int num = 0;
				for (DataSetRow row : ds) {
					if (num++ > 0) {
						throw new RuntimeException("Currently only datasets with one row are supported.");
					}
					replacements.putAll(getReplacementData(reportData, design, dsName, row));
				}
			}
			
			String prefix = getExpressionPrefix(design);
			String suffix = getExpressionSuffix(design);
			
			String templateContents = new String(r.getContents(), "UTF-8");
			pw = new PrintWriter(out);
			pw.write(EvaluationUtil.evaluateExpression(templateContents, replacements, prefix, suffix).toString());
		}
		catch (Exception e) {
			throw new RenderingException("Unable to render results due to: " + e, e);
		}
		finally {
			pw.close();
		}
	}
}
