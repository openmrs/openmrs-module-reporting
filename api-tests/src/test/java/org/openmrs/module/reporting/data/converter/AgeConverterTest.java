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