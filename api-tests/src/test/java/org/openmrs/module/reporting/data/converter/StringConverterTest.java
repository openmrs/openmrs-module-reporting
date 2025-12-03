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
import org.openmrs.module.reporting.data.converter.StringConverter;

public class StringConverterTest {
	
	/**
	 * @see StringConverter#convert(Object)
	 * @verifies convert an Object to a configured String representation
	 */
	@Test
	public void convert_shouldConvertAnObjectToAConfiguredStringRepresentation() throws Exception {
		StringConverter c = new StringConverter();
		c.getConversions().put("M", "Homme");
		c.getConversions().put("F", "Femme");
		c.setUnspecifiedValue("Inconnu");
		Assert.assertEquals("Homme", c.convert("M"));
		Assert.assertEquals("Femme", c.convert("F"));
		Assert.assertEquals("Inconnu", c.convert(""));
	}
}