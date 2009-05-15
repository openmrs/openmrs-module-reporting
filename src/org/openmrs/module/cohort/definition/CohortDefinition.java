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

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;

/**
 * Represents a search strategy for arriving at a cohort.<br/>
 * You evaluate a CohortDefinition using CohortEvaluator
 */
public interface CohortDefinition extends OpenmrsMetadata, Parameterizable  {
	
    /**
     * Returns a List of all Parameters that can be configured on this CohortDefinition.
     * Only some of these parameters will be configured on an instance.
     * For example:<br/>
     * PatientCharacteristicCohortDefinition might return Parameters for
     * gender, ageMin, ageMax, isDead, birthdateMin, birthdateMax
     * when you call this method.
     * <br/>
     * Then, when creating and persisting a specific instance of this filter to 
     * represent Males, you would call <code>configureParameter("gender", "M", true)</code>
     * 
     * @return - All available Parameters that one might add to this CohortDefinition
     */
	public List<Parameter> getAvailableParameters();
	
    /**
     * Retrieves the Parameter with the given name from the list of available parameter on this
     * CohortDefinition, and adds it as a Parameter on the instance with the given values
     */
	public void enableParameter(String name, Object defaultValue, boolean required);
	
}
