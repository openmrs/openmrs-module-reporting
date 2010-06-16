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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;

/**
 * Report Renderer implementation that supports rendering to an Excel template
 */
@Handler
@Localized("Text Template")
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
		
		// TODO: Implement more complex logic around multiple sheets for multiple rows / multiple datasets
		if (reportData.getDataSets().size() != 1) {
			throw new RuntimeException("Currently only one dataset is supported.");
		}
		Iterator<Map.Entry<String, DataSet>> datSetEntryIterator = reportData.getDataSets().entrySet().iterator();
		Map.Entry<String, DataSet> dataSetEntry = datSetEntryIterator.next();
		DataSetRow dataSetRow = (DataSetRow)dataSetEntry.getValue().iterator().next();
		if (datSetEntryIterator.hasNext()) {
			throw new RuntimeException("Currently only one dataset with one row is supported.");
		}
		
		PrintWriter pw = null;
		try {
			ReportDesign design = getDesign(argument);
			ReportDesignResource r = getTemplate(design);
			
			Map<String, Object> replacements = getReplacementData(reportData, design, dataSetEntry.getKey(), dataSetRow);
			String prefix = getExpressionPrefix(design);
			String suffix = getExpressionSuffix(design);
			
			String templateContents = new String(r.getContents(), "UTF-8");
			pw = new PrintWriter(out);
			for (Map.Entry<String, Object> entry : replacements.entrySet()) {
				templateContents = templateContents.replace(prefix + entry.getKey() + suffix, entry.getValue().toString());
			}
			pw.write(templateContents);
		}
		catch (Exception e) {
			throw new RenderingException("Unable to render results due to: " + e, e);
		}
		finally {
			pw.close();
		}
	}
}
