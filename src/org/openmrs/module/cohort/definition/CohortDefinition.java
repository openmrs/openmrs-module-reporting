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
package org.openmrs.module.cohort.definition;

import java.util.List;

import org.openmrs.module.cohort.definition.configuration.Property;
import org.openmrs.module.evaluation.Definition;

/**
 * Represents a search strategy for arriving at a cohort.<br/>
 * You evaluate a CohortDefinition using CohortEvaluator
 */
public interface CohortDefinition extends Definition  {
	
	/**
     * Returns a List of all Properties that can be configured on this CohortDefinition.
     * Only some of these will be configured on an instance.
     * @return - All available Properties that one might configure on this CohortDefinition
     */
	public List<Property> getConfigurationProperties();
	
}
