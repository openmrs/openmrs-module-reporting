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