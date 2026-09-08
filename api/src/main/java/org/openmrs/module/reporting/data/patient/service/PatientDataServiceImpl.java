/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.service;

import org.apache.commons.lang.time.StopWatch;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *  Base Implementation of the PersonQueryService API
 */
public class PatientDataServiceImpl extends BaseDefinitionService<PatientDataDefinition> implements PatientDataService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<PatientDataDefinition> getDefinitionType() {
		return PatientDataDefinition.class;
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 * @should evaluate a patient data definition
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedPatientData)super.evaluate(definition, context);
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	public EvaluatedPatientData evaluate(Mapped<? extends PatientDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedPatientData)super.evaluate(mappedDefinition, context);
	}

	@Override
	protected Evaluated<PatientDataDefinition> executeEvaluator(DefinitionEvaluator<PatientDataDefinition> evaluator, PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EvaluatedPatientData ret = new EvaluatedPatientData(definition, context);
		int batchSize = ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE();

		// Do not evaluate in batches if no base cohort is supplied, or no batch size is specified
		if (context.getBaseCohort() == null || batchSize <= 0 || context.getBaseCohort().size() <= batchSize) {
			return super.executeEvaluator(evaluator, definition, context);
		}

		if (context.getBaseCohort().size() > 0) {

			List<Cohort> batches = new ArrayList<Cohort>();
			List<Integer> ids = new ArrayList<Integer>(context.getBaseCohort().getMemberIds());
			for (int i=0; i<ids.size(); i+=batchSize) {
				batches.add(new Cohort(ids.subList(i, i + Math.min(batchSize, ids.size()-i))));
			}
			log.info("Number of batches to execute: " + batches.size());

			// Evaluate each batch
			for (Cohort batchCohort : batches) {
				EvaluationContext batchContext = context.shallowCopy();
				batchContext.setBaseCohort(batchCohort);
				batchContext.clearCache(); // Setting base cohort should do this, but just to be sure

				StopWatch timer = new StopWatch();
				timer.start();

				EvaluatedPatientData batchData = (EvaluatedPatientData)super.executeEvaluator(evaluator, definition, batchContext);
				ret.getData().putAll(batchData.getData());

				timer.stop();
				log.debug("Evaluated batch: " + timer.toString());
				log.debug("Number of running data evaluated: " + ret.getData().size());

				timer.reset();

				Context.flushSession();
				Context.clearSession();
			}
		}
		return ret;
	}
}
