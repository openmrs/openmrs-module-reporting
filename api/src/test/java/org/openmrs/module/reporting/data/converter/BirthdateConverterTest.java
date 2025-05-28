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