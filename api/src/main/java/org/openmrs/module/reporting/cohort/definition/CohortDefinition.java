/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.Patient;
import org.openmrs.module.reporting.query.Query;

/**
 * Represents a search strategy for arriving at a cohort.<br/>
 * You evaluate a CohortDefinition using CohortEvaluator
 */
public interface CohortDefinition extends Query<Patient>  {
	
}
