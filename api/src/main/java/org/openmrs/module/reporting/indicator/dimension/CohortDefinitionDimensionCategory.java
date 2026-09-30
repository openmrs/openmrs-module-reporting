/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.dimension;

import java.util.Map;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * The cohort definition dimension category uses a cohort definition to 
 * narrow patients into a specific category.  For example, the "male" category
 * would use the GenderCohortDefinition (gender=M) to narrow a set of patients
 * into the "male" category.
 */
public class CohortDefinitionDimensionCategory implements DimensionCategory {

	private String uniqueName;		// the unique name of this category (e.g. male or female)
	private Dimension dimension;  // the parent dimension (e.g.gender)
	private Mapped<CohortDefinition> cohortDefinition;	// the cohort definition that narrows patients in this category
		
	
	public CohortDefinitionDimensionCategory(String uniqueName, CohortDefinition cohortDefinition, Map<String,Object> parameterMapping) { 
		setUniqueName(uniqueName);
		setCohortDefinition(new Mapped<CohortDefinition>(cohortDefinition, parameterMapping));
	}
	
	
	public Dimension getDimension() { 
		return this.dimension;
	}
	
	public void setDimension(Dimension dimension) { 
		this.dimension = dimension;		
	}
	
	public String getUniqueName() {
		return uniqueName;
	}
	
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	
	public Mapped<CohortDefinition> getCohortDefinition() {
		return cohortDefinition;
	}
	
	public void setCohortDefinition(Mapped<CohortDefinition> cohortDefinition) {
		this.cohortDefinition = cohortDefinition;
	}
	
	
	
	
	
	
		
}
