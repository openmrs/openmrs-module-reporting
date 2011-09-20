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

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Converts an Age to one of a series of defined Age Ranges
 */
public class AgeRangeConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private List<AgeRange> ageRanges;
	
	//***** CONSTRUCTORS *****
	
	public AgeRangeConverter() {}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#converter(Object)
	 * @should convert an Age to a matching defined Age Range
	 * @should return null if the Age does not fall within an Age Range
	 */
	public Object convert(Object original) {
		Age age = (Age)original;
		for (AgeRange a : getAgeRanges()) {
			if (a.isInRange(age)) {
				return ObjectUtil.nvl(a.getLabel(), a.toString());
			}
		}
		return null;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Age.class;
	}
	
	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the ageRanges
	 */
	public List<AgeRange> getAgeRanges() {
		if (ageRanges == null) {
			ageRanges = new ArrayList<AgeRange>();
		}
		return ageRanges;
	}

	/**
	 * @param ageRanges the ageRanges to set
	 */
	public void setAgeRanges(List<AgeRange> ageRanges) {
		this.ageRanges = ageRanges;
	}
	
	/**
	 * @param ageRange the ageRange to add
	 */
	public void addAgeRange(AgeRange ageRange) {
		getAgeRanges().add(ageRange);
	}
}