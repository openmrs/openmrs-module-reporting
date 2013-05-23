package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.NumericObsCohortDefinition")
public class NumericObsCohortDefinition extends BaseObsCohortDefinition {
	
	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group="constraint1")
	RangeComparator operator1;
	
	@ConfigurationProperty(group="constraint1")
	Double value1;
	
	@ConfigurationProperty(group="constraint2")
	RangeComparator operator2;
	
	@ConfigurationProperty(group="constraint2")
	Double value2;

		
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


    /**
     * @return the operator1
     */
    public RangeComparator getOperator1() {
    	return operator1;
    }


	
    /**
     * @param operator1 the operator1 to set
     */
    public void setOperator1(RangeComparator operator1) {
    	this.operator1 = operator1;
    }


	
    /**
     * @return the operator2
     */
    public RangeComparator getOperator2() {
    	return operator2;
    }


	
    /**
     * @param operator2 the operator2 to set
     */
    public void setOperator2(RangeComparator operator2) {
    	this.operator2 = operator2;
    }


}
