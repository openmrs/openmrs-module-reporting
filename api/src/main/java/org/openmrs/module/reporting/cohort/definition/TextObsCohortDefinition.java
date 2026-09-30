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
