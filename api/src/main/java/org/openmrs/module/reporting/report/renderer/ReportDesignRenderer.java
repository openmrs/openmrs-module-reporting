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

import org.apache.commons.lang.StringUtils;
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
     * @return
     */
    protected String getFilenameBase(ReportRequest request) {
        String argument = request.getRenderingMode().getArgument();
        ReportDesign d = getDesign(argument);

        // Start with the default behavior
        String dateStr = DateUtil.formatDate(new Date(), "yyyy-MM-dd-HHmmss");
        String ret = request.getReportDefinition().getParameterizable().getName() + "_" + dateStr;

        // Try to evaluate a handlebars template as an alternative
        String template = d.getPropertyValue(FILENAME_BASE_PROPERTY, "{{request.reportDefinition.parameterizable.name}}_{{formatDate request.evaluateStartDatetime \"yyyy-MM-dd_HH:mm:ss\"}}");

        Map templateModel = new HashMap();
        templateModel.put("request", request);
        templateModel.put("argument", argument);

        TemplateFactory templateFactory = Context.getRegisteredComponents(TemplateFactory.class).get(0);
        try {
            String evaluatedTemplate = templateFactory.evaluateHandlebarsTemplate(template, templateModel);
            if (StringUtils.isNotBlank(evaluatedTemplate)) {
                ret = evaluatedTemplate;
            }
        }
        catch (EvaluationException e) {
            log.warn("Error evaluating filenameBase template (using default instead): " + template, e);
        }

        return ret;
    }

}
