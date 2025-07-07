/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.openmrs.api.ConceptService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.renderer.template.TemplateEvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class TemplateFactory {

    private MessageSourceService mss;

    private Handlebars handlebars = new Handlebars();

    @Autowired
    public TemplateFactory(@Qualifier("messageSourceService") MessageSourceService mss,
                           @Qualifier("conceptService") ConceptService conceptService) {
        this.mss = mss;
        this.handlebars.registerHelpers(new HandlebarsHelpers(mss, conceptService));
    }

    public Template compileHandlebarsTemplate(String template) {
        try {
            return handlebars.compileInline(template);
        } catch (IOException e) {
            throw new TemplateEvaluationException("Error compiling handlebars template: " + template, e); // this is in another package. Use this or another exception?
        }
    }

    public String evaluateHandlebarsTemplate(String template, Object context) throws EvaluationException {
        Template handlebarsTemplate = compileHandlebarsTemplate(template);
        try {
            return handlebarsTemplate.apply(context);
        } catch (IOException e) {
            throw new EvaluationException("template", e);
        }
    }

	public String evaluateHandlebarsTemplate(String template, Map<String, Object> parameterValues) throws EvaluationException {
		String ret = null;
		if (ObjectUtil.notNull(template)) {
			try {
				Template handlebarsTemplate = compileHandlebarsTemplate(template);
				return handlebarsTemplate.apply(parameterValues);
			}
			catch (Exception e) {
				throw new EvaluationException("template " + template, e);
			}
		}
		return ret;
	}
}
