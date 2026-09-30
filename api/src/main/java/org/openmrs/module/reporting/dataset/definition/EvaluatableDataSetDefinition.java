/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Normally each reporting definition is a simple DTO, and its logic lives in an associated evaluator. This class allows a
 * simpler approach, by letting concrete subclasses directly define an evaluate() method, instead of having this be done
 * in a separate class.
 * This combines well with
 * {@link org.openmrs.module.reporting.definition.library.implementerconfigured.BaseImplementerConfiguredDefinitionLibrary}
 */
public abstract class EvaluatableDataSetDefinition extends BaseDataSetDefinition {
	
	public abstract DataSet evaluate(EvaluationContext evalContext);

}
