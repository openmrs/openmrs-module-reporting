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
import org.openmrs.PersonName;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonNameConverterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see PersonNameConverter#convert(Object)
	 * @verifies convert a Person name into a String using a format expression
	 */
	@Test
	public void convert_shouldConvertAPersonNameIntoAStringUsingAFormatExpression() throws Exception {
		PersonName personName = new PersonName();
		personName.setGivenName("John");
		personName.setMiddleName("T");
		personName.setFamilyName("Smith");
		Object result = (new ObjectFormatter("{familyName}, {givenName}")).convert(personName);
		Assert.assertEquals("Smith, John", result.toString());
	}
}