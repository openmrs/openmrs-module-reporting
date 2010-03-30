package org.openmrs.module.reporting.cohort.definition;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("reporting.CodedObsCohortDefinition")
public class CodedObsCohortDefinition extends BaseObsCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group="constraint")
	SetComparator operator;
	
	@ConfigurationProperty(group="constraint")
	List<Concept> valueList;
	
	public CodedObsCohortDefinition() { }

	
    /**
     * @return the operator
     */
    public SetComparator getOperator() {
    	return operator;
    }

	
    /**
     * @param operator the operator to set
     */
    public void setOperator(SetComparator operator) {
    	this.operator = operator;
    }

	
    /**
     * @return the valueList
     */
    public List<Concept> getValueList() {
    	return valueList;
    }

	
    /**
     * @param valueList the valueList to set
     */
    public void setValueList(List<Concept> valueList) {
    	this.valueList = valueList;
    }

}
