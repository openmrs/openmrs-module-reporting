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
