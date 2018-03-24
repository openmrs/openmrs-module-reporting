/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ConvertedObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a ConvertedObsDataDefinition
 */
@Handler(supports=ConvertedObsDataDefinition.class, order=50)
public class ConvertedObsDataEvaluator implements ObsDataEvaluator {

    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedObsData c = new EvaluatedObsData(definition, context);
        ConvertedObsDataDefinition def = (ConvertedObsDataDefinition)definition;
        EvaluatedObsData unconvertedData = Context.getService(ObsDataService.class).evaluate(def.getDefinitionToConvert(), context);
        if (def.getConverters().isEmpty()) {
            c.setData(unconvertedData.getData());
        }
        else {
            for (Integer id : unconvertedData.getData().keySet()) {
                Object val = DataUtil.convertData(unconvertedData.getData().get(id), def.getConverters());
                c.addData(id, val);
            }
        }
        return c;
    }
}

