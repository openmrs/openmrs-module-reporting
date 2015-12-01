package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PropertyConverterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see PropertyConverter#convert(Object)
	 * @verifies convert an Object into it's property whose name is the configured format
	 */
	@Test
	public void convert_shouldConvertAnObjectIntoItsPropertyWhoseNameIsTheConfiguredFormat() throws Exception {
		EncounterType emergencyVisit = Context.getEncounterService().getEncounterType(2);
		PropertyConverter c = new PropertyConverter(EncounterType.class, "name");
		Assert.assertEquals(emergencyVisit.getName(), c.convert(emergencyVisit));
		c = new PropertyConverter(EncounterType.class, "description");
		Assert.assertEquals(emergencyVisit.getDescription(), c.convert(emergencyVisit));
	}

	/**
	 * @see PropertyConverter#convert(Object)
	 * @verifies convert an Object into it's string representation if not format is configured
	 */
	@Test
	public void convert_shouldConvertAnObjectIntoItsStringRepresentationIfNotFormatIsConfigured() throws Exception {
		EncounterType emergencyVisit = Context.getEncounterService().getEncounterType(2);
		PropertyConverter c = new PropertyConverter(EncounterType.class, "");
		Assert.assertEquals(emergencyVisit.toString(), c.convert(emergencyVisit));
	}
}