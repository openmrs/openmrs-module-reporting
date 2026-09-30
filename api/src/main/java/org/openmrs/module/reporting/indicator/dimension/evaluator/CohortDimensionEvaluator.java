/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.dimension.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimensionResult;
import org.openmrs.module.reporting.indicator.dimension.Dimension;

/**
 * Evaluates a CohortDimension
 */
@Handler(supports={CohortDimension.class})
public class CohortDimensionEvaluator implements DimensionEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Default Constructor
	 */
	public CohortDimensionEvaluator() {}

	/**
	 * @throws EvaluationException 
	 * @see DimensionEvaluator#evaluate(Dimension, EvaluationContext)
	 */
	public CohortDimensionResult evaluate(Dimension dimension, EvaluationContext context) throws EvaluationException {
		
		CohortDimension cd = (CohortDimension)dimension;
		CohortDimensionResult result = new CohortDimensionResult(cd, context);
		
		Cohort baseCohort = context.getBaseCohort();
		if (baseCohort == null) {
			baseCohort = Cohorts.allPatients(context);
		}
		
		Cohort totalDimensions = new Cohort();
		for (String key : dimension.getOptionKeys()) {
			Cohort currentCohort;
			try {
				currentCohort = evaluateDimensionOption(cd, key, context);
			} catch (Exception ex) { 
				throw new EvaluationException("dimension option: " + key, ex);
			}
			result.addOptionCohort(key, currentCohort);
			totalDimensions = CohortUtil.union(totalDimensions, currentCohort);
		}
		result.addOptionCohort(Dimension.UNCLASSIFIED, CohortUtil.subtract(baseCohort, totalDimensions));

		return result;
	}
	
	/**
	 * Evaluates the passed dimension option with the inputCohort as a basis
	 * @throws EvaluationException 
	 */
	public Cohort evaluateDimensionOption(CohortDimension dimension, String option, EvaluationContext context) throws EvaluationException {
		
		log.debug("Evaluating dimension: " + dimension + "." + option + "(" + context.getParameterValues() + ")");
	
		CohortDefinitionDimension d = (CohortDefinitionDimension) dimension;
		if (Dimension.UNCLASSIFIED.equalsIgnoreCase(option)) {
			CohortDimensionResult dimensionResult = evaluate(dimension, context);
			return dimensionResult.getCohort(Dimension.UNCLASSIFIED);
		}
		
		Mapped<CohortDefinition> mappedDef = d.getCohortDefinition(option);
		if (mappedDef == null) {
			throw new IllegalArgumentException("No CohortDefinition dimension option found for option: " + option);
		}
		Cohort found = Context.getService(CohortDefinitionService.class).evaluate(mappedDef, context);
		if (found == null) {
			return new Cohort();
		}
		if (context.getBaseCohort() != null) {
			return CohortUtil.intersect(context.getBaseCohort(), found);
		}
		return found;
	}
}