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