package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.api.PatientSetService.Modifier;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("reporting.NumericObsCohortDefinition")
public class NumericObsCohortDefinition extends BaseObsCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group="constraint1")
	Modifier modifier1;
	
	@ConfigurationProperty(group="constraint1")
	Double value1;
	
	@ConfigurationProperty(group="constraint2")
	Modifier modifier2;
	
	@ConfigurationProperty(group="constraint2")
	Double value2;

	
    /**
     * @return the modifier1
     */
    public Modifier getModifier1() {
    	return modifier1;
    }

	
    /**
     * @param modifier1 the modifier1 to set
     */
    public void setModifier1(Modifier modifier1) {
    	this.modifier1 = modifier1;
    }

	
    /**
     * @return the value1
     */
    public Double getValue1() {
    	return value1;
    }

	
    /**
     * @param value1 the value1 to set
     */
    public void setValue1(Double value1) {
    	this.value1 = value1;
    }

	
    /**
     * @return the modifier2
     */
    public Modifier getModifier2() {
    	return modifier2;
    }

	
    /**
     * @param modifier2 the modifier2 to set
     */
    public void setModifier2(Modifier modifier2) {
    	this.modifier2 = modifier2;
    }

	
    /**
     * @return the value2
     */
    public Double getValue2() {
    	return value2;
    }

	
    /**
     * @param value2 the value2 to set
     */
    public void setValue2(Double value2) {
    	this.value2 = value2;
    }


}
