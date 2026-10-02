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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.module.reporting.data.converter.BooleanConverter;

public class BooleanConverterTest {
	/**
	 * @see BooleanConverter#convert(Object)
	 * @verifies convert a Boolean to a configured text representation
	 */
	@Test
	public void convert_shouldConvertABooleanToAConfiguredTextRepresentation() throws Exception {
		
		BooleanConverter standardConverter = new BooleanConverter();
		Assertions.assertEquals(standardConverter.convert(Boolean.TRUE), "true");
		Assertions.assertEquals(standardConverter.convert(Boolean.FALSE), "false");
		Assertions.assertEquals(standardConverter.convert(null), "");
		
		BooleanConverter customConverter = new BooleanConverter("oui", "non", "?");
		Assertions.assertEquals(customConverter.convert(Boolean.TRUE), "oui");
		Assertions.assertEquals(customConverter.convert(Boolean.FALSE), "non");
		Assertions.assertEquals(customConverter.convert(null), "?");
	}
}