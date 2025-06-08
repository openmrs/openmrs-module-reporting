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

public class NullValueConverterTest {
	/**
	 * @see org.openmrs.module.reporting.data.converter.BooleanConverter#convert(Object)
	 * @verifies convert a Boolean to a configured text representation
	 */
	@Test
	public void convert_shouldConvertANullToAReplacementValue() throws Exception {
		NullValueConverter c = new NullValueConverter("Replacement value");
		Assert.assertEquals("Test", c.convert("Test"));
		Assert.assertEquals("Replacement value", c.convert(null));
	}
}