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