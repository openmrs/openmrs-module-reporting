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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.converter.DateConverter;

public class DateConverterTest {
	
	/**
	 * @see DateConverter#convert(Object)
	 * @verifies convert a Date into a String with the passed format
	 */
	@Test
	public void convert_shouldConvertADateIntoAStringWithThePassedFormat() throws Exception {
		Date today = DateUtil.getDateTime(2011, 4, 6);
		Assert.assertEquals("2011-04-06", (new DateConverter("yyyy-MM-dd")).convert(today));
		Assert.assertEquals("06/Apr/2011", (new DateConverter("dd/MMM/yyyy")).convert(today));
		Assert.assertEquals("06/avr./2011", (new DateConverter("dd/MMM/yyyy", null, Locale.FRENCH)).convert(today));
	}
}