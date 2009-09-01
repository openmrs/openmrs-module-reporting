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
package org.openmrs.module.reporting.web.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.IndicatorReportRenderer;
import org.openmrs.module.report.renderer.RenderingMode;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.web.renderers.CohortReportWebRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.report.CohortDataSet;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class ReportDataFormController extends SimpleFormController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		ReportData report = (ReportData) request.getSession().getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);
		if (Context.isAuthenticated() && report != null) {
			ReportService reportService = (ReportService) Context.getService(ReportService.class);
			List<RenderingMode> otherRenderingModes = new ArrayList<RenderingMode>(reportService.getRenderingModes(report.getDefinition()));
			for (Iterator<RenderingMode> i = otherRenderingModes.iterator(); i.hasNext();) {
				Class temp = i.next().getRenderer().getClass();
				if (temp.equals(CohortReportWebRenderer.class))
					i.remove();
			}
			ret.put("otherRenderingModes", otherRenderingModes);
		}
		return ret;
	}
	
	/**
	 * The onSubmit function receives the form/command object that was modified by the input form
	 * and saves it to the db
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj,
	                                BindException errors) throws Exception {
		
		ReportData report = (ReportData) request.getSession().getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);
		
		// If we're authorized to render a report, the report exists, and the user has requested 
		// "rerender" as the action, then we render the report using the appropriate report renderer 
		if (Context.isAuthenticated() && report != null && "rerender".equals(request.getParameter("action"))) {
			ReportDefinition reportDefinition = report.getDefinition();
			ReportService reportService = (ReportService) Context.getService(ReportService.class);
			String renderClass = request.getParameter("renderingMode");
			String renderArg = "";
			
			// 
			if (renderClass.indexOf("!") > 0) {
				int ind = renderClass.indexOf("!");
				renderArg = renderClass.substring(ind + 1);
				renderClass = renderClass.substring(0, ind);
			}
			
			// Figure out how to render the report
			//ReportRenderer renderer = reportService.getReportRenderer(renderClass);
			ReportRenderer renderer = new IndicatorReportRenderer();
			
			log.info("Re-rendering report with " + renderer.getClass() + " and argument " + renderArg);
			
			// If we're supposed to use a web report renderer, then we just redirect to the appropriate URL 
			if (renderer instanceof WebReportRenderer) {
				WebReportRenderer webRenderer = (WebReportRenderer) renderer;
				if (webRenderer.getLinkUrl(reportDefinition) != null) {
					request.getSession().setAttribute(ReportingConstants.OPENMRS_REPORT_DATA, report);
					request.getSession().setAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT, renderArg);
					String url = webRenderer.getLinkUrl(reportDefinition);
					if (!url.startsWith("/"))
						url = "/" + url;
					url = request.getContextPath() + url;
					return new ModelAndView(new RedirectView(url));
				}
			}
			
			// Otherwise, just render the report 
			// TODO it's possible that a web renderer will handle this -- is that ok?
			String filename = renderer.getFilename(reportDefinition, renderArg).replace(" ", "_");
			response.setContentType(renderer.getRenderedContentType(reportDefinition, renderArg));
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setHeader("Pragma", "no-cache");
			renderer.render(report, renderArg, response.getOutputStream());
			return null;
			
		}

		else {
			String view = getFormView();
			return new ModelAndView(new RedirectView(view));
		}
	}
	
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the
	 * form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@SuppressWarnings("unchecked")
	protected Object formBackingObject(HttpServletRequest request) throws ServletException {
		
		ReportData report = (ReportData) request.getSession().getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);
		
		if (null != report) {
			return report;
		} else {
			// Avoid the annoying NPE			
			MapDataSet emptyData = new MapDataSet();			
			emptyData.setName("empty");
			Map<String, DataSet> emptyMap = new HashMap<String, DataSet>();
			emptyMap.put("empty", emptyData);
			ReportDefinition emptySchema = new ReportDefinition();
			emptySchema.setName("empty");
			ReportData emptyReport = new ReportData();
			emptyReport.setDataSets(emptyMap);
			emptyReport.setDefinition(emptySchema);
			return emptyReport;
		}
	}
	
}
