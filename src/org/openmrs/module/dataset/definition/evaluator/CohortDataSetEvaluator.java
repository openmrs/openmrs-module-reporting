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
package org.openmrs.module.dataset.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.CohortDataSet;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;

/**
 * The logic that evaluates a {@link CohortDataSetDefinition} and produces a {@link CohortDataSet}
 * 
 * @see CohortDataSetDefinition
 * @see CohortDataSet
 */
@Handler(supports={CohortDataSetDefinition.class})
public class CohortDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public CohortDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		
		MapDataSet<Cohort> data = new MapDataSet<Cohort>();
		data.setDataSetDefinition(dataSetDefinition);
		data.setEvaluationContext(context);
		data.setName(dataSetDefinition.getName());

		CohortDataSetDefinition listDef = (CohortDataSetDefinition) dataSetDefinition;
		for (String key : listDef.getStrategies().keySet()) {
			Mapped<CohortDefinition> pd = listDef.getStrategies().get(key);
			EvaluationContext newEc = EvaluationContext.cloneForChild(context, pd);
			CohortDefinition cd = pd.getParameterizable();
			Cohort temp = Context.getService(CohortDefinitionService.class).evaluate(cd, newEc);
			if (context.getBaseCohort() != null) {
				temp = Cohort.intersect(temp, context.getBaseCohort());
			}
			data.addData(key, temp);
		}

		return data;
	}
}
