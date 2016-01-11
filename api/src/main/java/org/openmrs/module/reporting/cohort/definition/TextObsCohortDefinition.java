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
package org.openmrs.module.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.TextObsCohortDefinition")
public class TextObsCohortDefinition extends BaseObsCohortDefinition {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group="constraint")
	SetComparator operator;
	
	@ConfigurationProperty(group="constraint")
	List<String> valueList;

	public TextObsCohortDefinition() { }
	
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
    public List<String> getValueList() {
    	return valueList;
    }
	
    /**
     * @param valueList the valueList to set
     */
    public void setValueList(List<String> valueList) {
    	this.valueList = valueList;
    }
	
    /**
     * @param value the value to add to the list
     */
	public void addValue(String value) {
		if (valueList == null) {
			valueList = new ArrayList<String>();
		}
		valueList.add(value);
	}
}
