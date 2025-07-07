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