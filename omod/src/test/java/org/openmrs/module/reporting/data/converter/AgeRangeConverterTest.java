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
import org.openmrs.module.reporting.common.AgeRange;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.converter.AgeRangeConverter;

public class AgeRangeConverterTest {
	
	public AgeRangeConverter getConverter() {
		AgeRangeConverter c = new AgeRangeConverter();
		c.addAgeRange(new AgeRange(0, Age.Unit.YEARS, 17, Age.Unit.MONTHS, "<18m"));
		c.addAgeRange(new AgeRange(2, Age.Unit.YEARS, 17, Age.Unit.YEARS, "2y-17y"));
		return c;
	}
	
	
	/**
	 * @see AgeRangeConverter#convert(Object)
	 * @verifies convert an Age to a matching defined Age Range
	 */
	@Test
	public void convert_shouldConvertAnAgeToAMatchingDefinedAgeRange() throws Exception {
		Age sixMonthsOld = new Age(DateUtil.getDateTime(2011, 1, 1), DateUtil.getDateTime(2011, 7, 2));
		Assert.assertEquals("<18m", getConverter().convert(sixMonthsOld).toString());
		
		Age seventeenYearsOld = new Age(DateUtil.getDateTime(1994, 1, 1), DateUtil.getDateTime(2011, 7, 2));
		Assert.assertEquals("2y-17y", getConverter().convert(seventeenYearsOld).toString());
	}

	/**
	 * @see AgeRangeConverter#convert(Object)
	 * @verifies return null if the Age does not fall within an Age Range
	 */
	@Test
	public void convert_shouldReturnNullIfTheAgeDoesNotFallWithinAnAgeRange() throws Exception {
		Age eighteenMonthsOld = new Age(DateUtil.getDateTime(2010, 1, 1), DateUtil.getDateTime(2011, 7, 1));
		Object range = getConverter().convert(eighteenMonthsOld);
		Assert.assertNull(range);
	}
}