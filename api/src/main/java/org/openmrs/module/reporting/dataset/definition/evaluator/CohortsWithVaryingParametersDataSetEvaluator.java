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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortsWithVaryingParametersDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 *
 */
@Handler(supports={CohortsWithVaryingParametersDataSetDefinition.class})
public class CohortsWithVaryingParametersDataSetEvaluator implements DataSetEvaluator {

    private final Log log = LogFactory.getLog(getClass());

    private Handlebars handlebars = new Handlebars();

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
        CohortsWithVaryingParametersDataSetDefinition dsd = (CohortsWithVaryingParametersDataSetDefinition) dataSetDefinition;
        MapDataSet results = new MapDataSet(dsd, evalContext);

        for (Mapped<? extends CohortDefinition> mcd : dsd.getCohortDefinitions()) {
            CohortDefinition cd = mcd.getParameterizable();

            Template nameTemplate = null;
            if (cd.getName() != null) {
                try {
                    nameTemplate = handlebars.compileInline(cd.getName());
                } catch (Exception e) {
                    throw new EvaluationException("error compiling name template", e);
                }
            }

            Template labelTemplate = null;
            if (cd.getDescription() != null) {
                try {
                    labelTemplate = handlebars.compileInline(cd.getDescription());
                } catch (Exception e) {
                    throw new EvaluationException("error compiling label template", e);
                }
            }

            for (Map<String, Object> parameterOption : dsd.getVaryingParameters()) {
                EvaluationContext contextForVariation = evalContext.shallowCopy();
                contextForVariation.getParameterValues().putAll(parameterOption);
                EvaluatedCohort cohort = cohortDefinitionService.evaluate(mcd, contextForVariation);

                String name = null;
                if (nameTemplate != null) {
                    try {
                        name = nameTemplate.apply(contextForVariation.getParameterValues());
                    } catch (IOException e) {
                        log.warn("Error evaluating name template", e);
                    }
                }
                if (name == null) {
                    name = cd.getName();
                }

                String label = null;
                if (labelTemplate != null) {
                    try {
                        label = labelTemplate.apply(contextForVariation.getParameterValues());
                    } catch (IOException e) {
                        log.warn("Error evaluating label template", e);
                    }
                }
                if (label == null) {
                    label = cd.getDescription();
                }

                DataSetColumn column = new DataSetColumn(name, label, EvaluatedCohort.class);
                results.addData(column, cohort);
            }
        }

        return results;
    }

}
