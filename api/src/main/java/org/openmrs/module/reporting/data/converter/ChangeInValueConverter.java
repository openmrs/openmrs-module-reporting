/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes in a List of objects in a pre-defined order, a Converter to get the value to check,
 * and returns a new List that removes any elements that have the same value as the one which preceeded it
 */
public class ChangeInValueConverter implements DataConverter {

	//***** PROPERTIES *****

    private DataConverter valueConverter;

	//***** CONSTRUCTORS *****

    public ChangeInValueConverter() {}

	public ChangeInValueConverter(DataConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 */
	@SuppressWarnings("rawtypes")
	public Object convert(Object original) {
		List l = (List) original;
		if (l != null) {
            List ret = new ArrayList();
            Object lastValue = null;
            for (Object currentValue : l) {
                Object comparisonValue = currentValue;
                if (valueConverter != null) {
                    comparisonValue = valueConverter.convert(currentValue);
                }
                if (lastValue == null || !lastValue.equals(comparisonValue)) {
                    ret.add(currentValue);
                }
                lastValue = comparisonValue;
            }
            return ret;
		}
		return null;
	}

	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
        return List.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return List.class;
	}
	
	//***** PROPERTY ACCESS *****

    public DataConverter getValueConverter() {
        return valueConverter;
    }

    public void setValueConverter(DataConverter valueConverter) {
        this.valueConverter = valueConverter;
    }
}