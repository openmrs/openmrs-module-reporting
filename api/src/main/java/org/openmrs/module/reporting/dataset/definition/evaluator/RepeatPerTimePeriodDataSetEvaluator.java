/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TimePeriod;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RepeatPerTimePeriodDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Delegates to evaluating a MultiParameterDataSet after producing a specific set of iterations on the fly
 */
@Handler(supports={RepeatPerTimePeriodDataSetDefinition.class})
public class RepeatPerTimePeriodDataSetEvaluator implements DataSetEvaluator {

    @Autowired
    private DataSetDefinitionService dataSetDefinitionService;

    public void setDataSetDefinitionService(DataSetDefinitionService dataSetDefinitionService) {
        this.dataSetDefinitionService = dataSetDefinitionService;
    }

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
        RepeatPerTimePeriodDataSetDefinition dsd = (RepeatPerTimePeriodDataSetDefinition) dataSetDefinition;

        Mapped<? extends DataSetDefinition> baseMappedDef = dsd.getBaseDefinition();

        MultiParameterDataSetDefinition delegate = new MultiParameterDataSetDefinition(baseMappedDef.getParameterizable());

        TimePeriod period = dsd.getRepeatPerTimePeriod();
        if (period == null) {
            throw new IllegalArgumentException("repeatPerTimePeriod is required");
        }

        DateTime thisPeriodStart = new DateTime(((Date) evalContext.getParameterValue("startDate")).getTime());
        DateTime end = new DateTime(DateUtil.getEndOfDayIfTimeExcluded((Date) evalContext.getParameterValue("endDate")).getTime());

        while (thisPeriodStart.isBefore(end)) {
            DateTime nextPeriodStart = thisPeriodStart.plus(period.getJodaPeriod());
            boolean lastIteration = !nextPeriodStart.isBefore(end); // i.e. nextPeriodStart >= end
            DateTime thisPeriodEnd;
            if (lastIteration) {
                thisPeriodEnd = end;
            }
            else {
                thisPeriodEnd = nextPeriodStart.minus(Duration.millis(1));
            }

            Map<String, Object> startAndEndDate = new HashMap<String, Object>();
            startAndEndDate.put("startDate", thisPeriodStart.toDate());
            startAndEndDate.put("endDate", thisPeriodEnd.toDate());
            Map<String, Object> iteration = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : baseMappedDef.getParameterMappings().entrySet()) {
                String iterationParamName = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    Object evaluated = EvaluationUtil.evaluateExpression((String) value, startAndEndDate);
                    // expressions based on parameters other than startDate/endDate will come out like ${loc} -> "loc"
                    if (value.equals(EvaluationUtil.EXPRESSION_START + evaluated + EvaluationUtil.EXPRESSION_END)) {
                        continue;
                    }
                    value = evaluated;
                }
                iteration.put(iterationParamName, value);
            }
            delegate.addIteration(iteration);

            thisPeriodStart = nextPeriodStart;
        }

        return dataSetDefinitionService.evaluate(delegate, evalContext);
    }

}
