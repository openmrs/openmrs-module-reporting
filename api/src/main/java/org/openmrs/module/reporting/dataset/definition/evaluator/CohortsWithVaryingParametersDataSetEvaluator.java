/*
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

package org.openmrs.module.reporting.dataset.definition.evaluator;

import com.github.jknack.handlebars.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortsWithVaryingParametersDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.template.TemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * Evaluates each CohortDefinition against each set of parameter options.
 * Produces a MapDataSet whose column names are given by the CohortDefinition's name (which should be a handlebars
 * template that can refer to parameter values, e.g. "at-{{ location.uuid }}". Labels of the columns in the resulting
 * data set are given by the CohortDefinition's description (which should be a handlebars template that can refer to
 * parameter values, e.g. "Registered at {{ location.name }}").
 */
@Handler(supports={CohortsWithVaryingParametersDataSetDefinition.class})
public class CohortsWithVaryingParametersDataSetEvaluator implements DataSetEvaluator {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    TemplateFactory templateFactory;

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
        CohortsWithVaryingParametersDataSetDefinition dsd = (CohortsWithVaryingParametersDataSetDefinition) dataSetDefinition;
        SimpleDataSet results = new SimpleDataSet(dsd, evalContext);

        Template rowLabelTemplate = templateFactory.compileHandlebarsTemplate(dsd.getRowLabelTemplate());
        DataSetColumn rowLabelColumn = new DataSetColumn("rowLabel", "", String.class);

        for (CohortsWithVaryingParametersDataSetDefinition.Column column : dsd.getColumns()) {
            if (column.getLabelTemplate() != null) {
                Template template = templateFactory.compileHandlebarsTemplate(column.getLabelTemplate());
                try {
                    column.setLabel(template.apply(evalContext.getParameterValues()));
                } catch (IOException ex) {
                    throw new EvaluationException("column " + column.getLabelTemplate(), ex);
                }
            }
        }

        for (Map<String, Object> parameterOption : dsd.getVaryingParameters()) {
            DataSetRow row = new DataSetRow();
            try {
                row.addColumnValue(rowLabelColumn, rowLabelTemplate.apply(parameterOption));
            } catch (IOException e) {
                throw new EvaluationException("rowLabelTemplate", e);
            }

            for (CohortsWithVaryingParametersDataSetDefinition.Column column : dsd.getColumns()) {
                EvaluationContext contextForVariation = evalContext.shallowCopy();
                contextForVariation.getParameterValues().putAll(parameterOption);
                EvaluatedCohort cohort = cohortDefinitionService.evaluate(column.getCohortDefinition(), contextForVariation);
                row.addColumnValue(column, cohort);
            }

            results.addRow(row);
        }

        return results;
    }

}
