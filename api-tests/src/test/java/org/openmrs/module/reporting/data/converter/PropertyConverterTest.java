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
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class PropertyConverterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see PropertyConverter#convert(Object)
	 * @verifies convert an Object into it's property whose name is the configured format
	 */
	@Test
	public void convert_shouldConvertAnObjectIntoItsPropertyWhoseNameIsTheConfiguredFormat() throws Exception {
		EncounterType emergencyVisit = Context.getEncounterService().getEncounterType(2);
		PropertyConverter c = new PropertyConverter(EncounterType.class, "name");
		Assertions.assertEquals(emergencyVisit.getName(), c.convert(emergencyVisit));
		c = new PropertyConverter(EncounterType.class, "description");
		Assertions.assertEquals(emergencyVisit.getDescription(), c.convert(emergencyVisit));
	}

	/**
	 * @see PropertyConverter#convert(Object)
	 * @verifies convert an Object into it's string representation if not format is configured
	 */
	@Test
	public void convert_shouldConvertAnObjectIntoItsStringRepresentationIfNotFormatIsConfigured() throws Exception {
		EncounterType emergencyVisit = Context.getEncounterService().getEncounterType(2);
		PropertyConverter c = new PropertyConverter(EncounterType.class, "");
		Assertions.assertEquals(emergencyVisit.toString(), c.convert(emergencyVisit));
	}
}