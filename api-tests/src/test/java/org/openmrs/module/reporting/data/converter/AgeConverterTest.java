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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.converter.AgeConverter;

public class AgeConverterTest {
	
	/**
	 * @return a new Age for someone who is 36 years, 4 months old
	 */
	public Age getAgeToTest() {
		return new Age(DateUtil.getDateTime(1975, 4, 8), DateUtil.getDateTime(2011, 9, 6));
	}
	
	/**
	 * @see AgeConverter#convert(Object)
	 * @verifies convert an Age to integer years
	 */
	@Test
	public void convert_shouldConvertAnAgeToIntegerYears() throws Exception {
		Object conversion = (new AgeConverter(AgeConverter.YEARS)).convert(getAgeToTest());
		Assert.assertEquals("36", conversion.toString());
		Assert.assertEquals(Integer.class, conversion.getClass());
	}

	/**
	 * @see AgeConverter#convert(Object)
	 * @verifies convert an Age to integer months
	 */
	@Test
	public void convert_shouldConvertAnAgeToIntegerMonths() throws Exception {
		Object conversion = (new AgeConverter(AgeConverter.MONTHS)).convert(getAgeToTest());
		Assert.assertEquals("436", conversion.toString());
		Assert.assertEquals(Integer.class, conversion.getClass());
	}

	/**
	 * @see AgeConverter#convert(Object)
	 * @verifies convert an Age to a formatted string
	 */
	@Test
	public void convert_shouldConvertAnAgeToAFormattedString() throws Exception {
		Object conversion = (new AgeConverter("I am {y} years and {m} months old")).convert(getAgeToTest());
		Assert.assertEquals("I am 36 years and 4 months old", conversion.toString());
		Assert.assertEquals(String.class, conversion.getClass());
		
		conversion = (new AgeConverter("I am {m} months old")).convert(getAgeToTest());
		Assert.assertEquals("I am 436 months old", conversion.toString());
		Assert.assertEquals(String.class, conversion.getClass());
	}
}