package org.openmrs.module.reporting.dataset.column.converter;

import junit.framework.Assert;

import org.junit.Test;

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