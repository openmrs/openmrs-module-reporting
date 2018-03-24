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
import org.openmrs.PersonAddress;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonAddressConverterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see PersonAddressConverter#convert(Object)
	 * @verifies convert a Person name into a String using a format expression
	 */
	@Test
	public void convert_shouldConvertAPersonAddressIntoAStringUsingAFormatExpression() throws Exception {
		PersonAddress pa = new PersonAddress();
		pa.setCountyDistrict("Suffolk");
		pa.setCityVillage("Boston");
		pa.setStateProvince("MA");
		pa.setCountry("USA");
		Object result = (new ObjectFormatter("{cityVillage}, {stateProvince}")).convert(pa);
		Assert.assertEquals("Boston, MA", result.toString());
	}
}