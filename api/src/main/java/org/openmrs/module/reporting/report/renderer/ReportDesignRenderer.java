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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.template.TemplateFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renderers which use design files to influence the rendered report output
 * should extend this base renderer class
 */
public abstract class ReportDesignRenderer extends AbstractReportRenderer  {

    private transient Log log = LogFactory.getLog(this.getClass());

	public static final String SORT_WEIGHT_PROPERTY = "sortWeight";
    public static final String FILENAME_BASE_PROPERTY = "filenameBase";

    /**
	 * @see ReportRenderer#getRenderingModes(ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		List<RenderingMode> ret = new ArrayList<RenderingMode>();
		List<ReportDesign> designs = Context.getService(ReportService.class).getReportDesigns(definition, getClass(), false);
		for (ReportDesign d : designs) {
			Integer sortWeight = 100;
			try {
				sortWeight = Integer.valueOf(d.getPropertyValue(SORT_WEIGHT_PROPERTY, "100"));
			}
			catch (Exception e) {}
			ret.add(new RenderingMode(this, d.getName(), d.getUuid(), sortWeight));
		}
		return ret;
	}
	
	/**
	 * Returns the selected ReportDesign, given the rendering mode argument
	 * @param argument
	 * @return
	 */
	public ReportDesign getDesign(String argument) {
		ReportDesign design = Context.getService(ReportService.class).getReportDesignByUuid(argument); 
		return design != null ? design : new ReportDesign();
	}

    /**
     * Helper method that subclasses can use in their getFilename method, that does generates the base
     * of the filename (with no extension) based on a "filenameTemplate" design argument, which defaults
     * to "{{definition.name}}_{{dateYmd}}-{{timeHms}}"
     * @param request
     * @param argument
     * @return
     */
    protected String getFilenameBase(ReportRequest request, String argument) {
        ReportDesign d = getDesign(argument);
        String template = d.getPropertyValue(FILENAME_BASE_PROPERTY, "{{request.reportDefinition.parameterizable.name}}_{{formatDate request.evaluateStartDatetime \"yyyy-MM-dd_HH:mm:ss\"}}");

        Map templateModel = new HashMap();
        templateModel.put("request", request);
        templateModel.put("argument", argument);

        TemplateFactory templateFactory = Context.getRegisteredComponents(TemplateFactory.class).get(0);
        try {
            return templateFactory.evaluateHandlebarsTemplate(template, templateModel);
        } catch (EvaluationException e) {
            log.warn("Error evaluating filenameBase template (using default instead): " + template, e);

            // fall back to old behavior
            String dateStr = DateUtil.formatDate(new Date(), "yyyy-MM-dd-HHmmss");
            return request.getReportDefinition().getParameterizable().getName() + "_" + dateStr;
        }
    }

}
