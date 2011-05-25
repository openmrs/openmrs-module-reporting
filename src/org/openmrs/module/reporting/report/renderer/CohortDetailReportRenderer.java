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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.serialization.SerializationException;

/**
 * Supports rendering a series of Cohorts with particular datasets
 */
@Handler
@Localized("reporting.CohortDetailReportRenderer")
public class CohortDetailReportRenderer extends ReportDesignRenderer {
	
	/**
     * @see ReportRenderer#getRenderedContentType(ReportDefinition, String)
     */
    public String getRenderedContentType(ReportDefinition schema, String argument) {
		if ("excel".equalsIgnoreCase(argument)) {
			return "application/vnd.ms-excel";
		}
    	return "text/html";
    }

	/**
	 * @see ReportRenderer#getLinkUrl(ReportDefinition)
	 */
	public String getLinkUrl(ReportDefinition schema) {
		return null;
	}
	
	/**
	 * @see ReportRenderer#getFilename(ReportDefinition)
	 */
	public String getFilename(ReportDefinition schema, String argument) {
		String[] split = argument.split(":");
		return schema.getName() + "." + split[1];
	}
	
	/**
	 * @see ReportRenderer#getRenderingModes(ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		List<RenderingMode> ret = new ArrayList<RenderingMode>();
		int index = 100;
		List<ReportDesign> designs = Context.getService(ReportService.class).getReportDesigns(definition, getClass(), false);
		for (ReportDesign d : designs) {
			ret.add(new RenderingMode(this, d.getName() + " (html)", d.getUuid() + ":html", index++));
			ret.add(new RenderingMode(this, d.getName() + " (xls)", d.getUuid() + ":xls", index++));
		}
		return ret;
	}

	/**
	 * @throws  
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	@SuppressWarnings("unchecked")
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		String[] args = argument.split(":");
		ReportDesign design = getDesign(args[0]);
		ReportDesignResource resource = design.getResourceByName("designFile");
		Map<String, String> parameterValues = new LinkedHashMap<String, String>();
		for (Map.Entry<String, Object> e : results.getContext().getParameterValues().entrySet()) {
			Parameter p = results.getDefinition().getParameter(e.getKey());
			if (p != null) {
				String value = (e.getValue() instanceof Date ? Context.getDateFormat().format((Date)e.getValue()) : e.getValue().toString());
				parameterValues.put(p.getLabelOrName(), value);
			}
		}
		
		// Collect all available Cohorts by key
		Map<String, Cohort> cohorts = new HashMap<String, Cohort>();
		Map<String, String> cohortLabels = new HashMap<String, String>();
		boolean usePrefix = results.getDataSets().size() > 1;
		for (String dsKey : results.getDataSets().keySet()) {
			String prefix = usePrefix ? dsKey + "." : "";
			DataSet ds = results.getDataSets().get(dsKey);
			if (ds instanceof MapDataSet) {
				MapDataSet mds = (MapDataSet)ds;
				for (DataSetColumn column : mds.getMetaData().getColumns()) {
					String cohortKey = prefix + column.getName();
					Object colValue = mds.getData(column);
					Cohort c = null;
					if (colValue instanceof Cohort) {
						c = (Cohort)colValue;
					}
					else if (colValue instanceof CohortIndicatorAndDimensionResult) {
						c = ((CohortIndicatorAndDimensionResult)colValue).getCohortIndicatorAndDimensionCohort();
					}
					if (c != null) {
						cohorts.put(cohortKey, c);
						cohortLabels.put(cohortKey, column.getLabel());
					}
				}
			}
		}
		
		Map<String, Mapped<? extends DataSetDefinition>> m = null;
		try {
			ReportingSerializer s = new ReportingSerializer();
			m = s.deserialize(new String(resource.getContents()), Map.class);
		}
		catch (SerializationException e) {
			throw new RenderingException("Error deserializing the design file.", e);
		}
		
		// Iterate across all of the defined data sets to show, and evaluate them
		Map<String, DataSet> datasets = new LinkedHashMap<String, DataSet>();
		DataSetDefinitionService svc = Context.getService(DataSetDefinitionService.class);
		for (Map.Entry<String, Mapped<? extends DataSetDefinition>> e : m.entrySet()) {
			Cohort c = cohorts.get(e.getKey());
			if (c != null) {
				EvaluationContext ctx = EvaluationContext.clone(results.getContext());
				if (ctx.getBaseCohort() == null) {
					ctx.setBaseCohort(c);
				}
				else {
					ctx.setBaseCohort(Cohort.intersect(ctx.getBaseCohort(), c));
				}
				try {
					DataSet ds = svc.evaluate(e.getValue(), ctx);
					datasets.put(e.getKey(), ds);
				} catch (Exception ex) {
					throw new RenderingException("Error evaluating dataset " + e.getKey(), new EvaluationException("dataset: " + e.getKey(), ex));
				}
			}
		}
		
		// Not, render it depending on the argument passed in
		if ("xls".equalsIgnoreCase(args[1])) {
	        HSSFWorkbook wb = new HSSFWorkbook();
	        ExcelStyleHelper styleHelper = new ExcelStyleHelper(wb);

			// For each dataset that is defined to be included, evaluate and include it
			for (String dataSetKey : datasets.keySet()) {
				DataSet dataset = datasets.get(dataSetKey);
				
				String displayName = cohortLabels.get(dataSetKey);
		        HSSFSheet sheet = wb.createSheet(ExcelSheetHelper.fixSheetName(displayName));
	            ExcelSheetHelper helper = new ExcelSheetHelper(sheet);
				
	            helper.addCell(displayName, styleHelper.getStyle("bold"));
	            helper.nextRow();
	            StringBuilder params = new StringBuilder();
				for (Iterator<String> i = parameterValues.keySet().iterator(); i.hasNext();) {
					String key = i.next();
					params.append(key + ": " + parameterValues.get(key) + (i.hasNext() ? ", " : ""));								
				}
				helper.addCell(params.toString());
				helper.nextRow();
				helper.nextRow();
				for (DataSetColumn column : dataset.getMetaData().getColumns()) {
					helper.addCell(column.getLabel(), styleHelper.getStyle("bold"));
				}
				helper.nextRow();
				for (DataSetRow row : dataset) {
					for (DataSetColumn column : dataset.getMetaData().getColumns()) {
						Object cellValue = row.getColumnValue(column);
	                    HSSFCellStyle style = null;
	                    if (cellValue instanceof Date) {
	                        style = styleHelper.getStyle("date");
	                    }
	                    helper.addCell(cellValue, style);
					}
					helper.nextRow();
				}
			}		
	        wb.write(out);
		}
		else {
			Writer w = new OutputStreamWriter(out,"UTF-8");
			
			// First output the name, description, and parameters of the report
			w.write("<h4>" + results.getDefinition().getName() + "</h4>");		
			w.write("<small>");
			for (Iterator<String> i = parameterValues.keySet().iterator(); i.hasNext();) {
				String key = i.next();
				w.write(key + ": <strong>" + parameterValues.get(key) + "</strong>" + (i.hasNext() ? " | " : ""));								
			}
			w.write("</small>");
			
			// For each dataset that is defined to be included, evaluate and include it
			for (String dataSetKey : datasets.keySet()) {
				DataSet dataset = datasets.get(dataSetKey);
				w.write("<br/><br/><strong>" + cohortLabels.get(dataSetKey) + "</strong>");
				w.write("<table id=\"indicator-report-dataset-" + dataSetKey +"\" class=\"display indicator-report-dataset\" border=1>");
				w.write("<tr>");
				for (DataSetColumn column : dataset.getMetaData().getColumns()) {
					w.write("<th>"+column.getLabel()+"</th>");
				}
				w.write("</tr>");
				for (DataSetRow row : dataset) {
					w.write("<tr>");
					for (DataSetColumn column : dataset.getMetaData().getColumns()) {
						Object cellValue = row.getColumnValue(column.getName());
						w.write("<td>" + ObjectUtil.nvlStr(cellValue, "") + "</td>");
					}
					w.write("</tr>");
				}
				w.write("</table>");
			}		
			w.flush();
		}
	}
}
