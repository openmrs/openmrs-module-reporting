package org.openmrs.module.reporting.data.converter;

import java.util.Date;

import org.junit.Assert;

import org.junit.Test;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.converter.BirthdateToAgeConverter;

public class BirthdateToAgeConverterTest {
	
	/**
	 * @see BirthdateToAgeConverter#convert(Object)
	 * @verifies convert a birthdate to an age on the configured date
	 */
	@Test
	public void convert_shouldConvertABirthdateToAnAgeOnTheConfiguredDate() throws Exception {
		Birthdate birthdate = new Birthdate(DateUtil.getDateTime(1975, 4, 8));
		Date today = DateUtil.getDateTime(2011, 9, 6);
		Age age = (Age)(new BirthdateToAgeConverter(today)).convert(birthdate);
		Assert.assertEquals(36, age.getFullYears().intValue());
		Assert.assertEquals(4, age.getFullMonthsSinceLastBirthday().intValue());
	}
}