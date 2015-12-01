package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;

import org.junit.Test;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class BirthdateConverterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see BirthdateConverter#convert(Object)
	 * @verifies convert a birthdate to String
	 */
	@Test
	public void convert_shouldConvertABirthdateToAFormattedString() throws Exception {
		BirthdateConverter c = new BirthdateConverter("dd/MMM/yyyy", "~yyyy");
		Birthdate birthdate = new Birthdate(DateUtil.getDateTime(1975, 4, 8));
		Assert.assertEquals("08/Apr/1975", c.convert(birthdate));
		birthdate.setEstimated(true);
		Assert.assertEquals("~1975", c.convert(birthdate));
	}
}