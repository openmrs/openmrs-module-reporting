/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * A class that implements this interface indicates that it represents
 * a definition that can be evaluated in the context of an EvaluationContext
 * 
 * @see ReportDefinition
 * @see DataSetDefinition
 * @see CohortDefinition
 * @see ReportDefinition
 * @see EvaluationContext
 */
public interface Definition extends OpenmrsMetadata, Parameterizable {
	
}
