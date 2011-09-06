package org.openmrs.module.reporting.dataset.column.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;

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