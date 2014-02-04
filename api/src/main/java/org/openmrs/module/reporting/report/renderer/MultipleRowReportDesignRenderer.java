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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSetMetaData;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;

/**
 * Support ability for ReportTemplateRenderer to render one output per row in a given dataset.
 * REPORT-438 If a given renderer wants to automatically get this ability, just wrap it in this. e.g
 * ReportRendert renderer = new MultipleRowReportDesignRenderer(new XmlReportRenderer());
 */
public class MultipleRowReportDesignRenderer extends ReportDesignRenderer {
	
	public static final String REPEAT_TEMPLATE_FOR_DATASET = "repeatTemplateForDataset";
	
	private ReportRenderer renderer;
	
	public MultipleRowReportDesignRenderer(ReportRenderer renderer) {
		if (renderer instanceof MultipleRowReportDesignRenderer || renderer instanceof WebReportRenderer) {
			throw new IllegalArgumentException("Cannot be a MultipleRowReportDesignRenderer or WebReportRenderer");
		}
		
		this.renderer = renderer;
	}
	
	public MultipleRowReportDesignRenderer(String format) {
		renderer = new TextTemplateRenderer();
		
		if ("xml".equalsIgnoreCase(format)) {
			renderer = new XmlReportRenderer();
		} else if ("csv".equalsIgnoreCase(format)) {
			renderer = new CsvReportRenderer();
		} else if ("tsv".equalsIgnoreCase(format)) {
			renderer = new TsvReportRenderer();
		} else if ("xls".equalsIgnoreCase(format)) {
			renderer = new XlsReportRenderer();
		} else if ("html".equalsIgnoreCase(format)) {
			renderer = new SimpleHtmlReportRenderer();
		}
	}
	
	/**
	 * @see ReportRenderer#getFilename(ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition reportDefinition, String argument) {
		return renderer.getFilename(reportDefinition, argument);
	}
	
	/**
	 * @see ReportRenderer#getRenderedContentType(ReportDefinition, String)
	 */
	public String getRenderedContentType(ReportDefinition schema, String argument) {
		return renderer.getRenderedContentType(schema, argument);
	}
	
	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		ReportDesign reportDesign = getDesign(argument);
		String repeatTemplateForDataset = null;
		if (reportDesign != null) {
			repeatTemplateForDataset = reportDesign.getPropertyValue(REPEAT_TEMPLATE_FOR_DATASET, null);
		}
		
		if (StringUtils.isBlank(repeatTemplateForDataset)) {
			renderer.render(results, argument, out);
		} else {
			Map<String, DataSet> dataSets = results.getDataSets();
			List<ByteArrayOutputStream> filesToZip = new ArrayList<ByteArrayOutputStream>();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			boolean atleastAnonZippedDataSet = false;
			
			for (String dsKey : dataSets.keySet()) {
				DataSet dataset = dataSets.get(dsKey);
				for (DataSetRow row : dataset) {
					ReportData reportData = getReportDataWithRow(results, dataset, dsKey, row);
					if (dsKey.equals(repeatTemplateForDataset)) {
						ByteArrayOutputStream bs = new ByteArrayOutputStream();
						renderer.render(results, argument, bs);
						filesToZip.add(bs);
					} else {
						atleastAnonZippedDataSet = true;
						renderer.render(reportData, argument, baos);
					}
				}
			}
			
			if (filesToZip.size() == 0) {
				out.write(baos.toByteArray());
			} else {
				ZipOutputStream zos = new ZipOutputStream(out);
				
				if (atleastAnonZippedDataSet) {
					ZipEntry zipEntry = new ZipEntry("File");
					zos.putNextEntry(zipEntry);
					zos.write(baos.toByteArray(), 0, baos.size());
					zos.closeEntry();
				}
				
				int index = 1;
				for (ByteArrayOutputStream byteStream : filesToZip) {
					ZipEntry zipEntry = new ZipEntry("File" + index++);
					zos.putNextEntry(zipEntry);
					zos.write(byteStream.toByteArray(), 0, byteStream.size());
					zos.closeEntry();
				}
				
				zos.flush();
			}
		}
	}
	
	private ReportData getReportDataWithRow(ReportData reportData, DataSet dataset, String dsKey, DataSetRow row) {
		MapDataSet clonedDataSet = new MapDataSet(dataset.getDefinition(), dataset.getContext());
		clonedDataSet.setMetaData((SimpleDataSetMetaData) dataset.getMetaData());
		clonedDataSet.addRow(row);
		
		Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
		dataSets.put(dsKey, clonedDataSet);
		
		reportData.setDataSets(dataSets);
		return reportData;
	}
}
