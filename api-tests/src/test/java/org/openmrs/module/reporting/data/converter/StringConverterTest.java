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