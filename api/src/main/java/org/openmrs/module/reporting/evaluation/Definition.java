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
