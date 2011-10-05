package org.openmrs.module.reporting.data.converter;


import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.data.converter.BooleanConverter;

public class BooleanConverterTest {
	/**
	 * @see BooleanConverter#convert(Object)
	 * @verifies convert a Boolean to a configured text representation
	 */
	@Test
	public void convert_shouldConvertABooleanToAConfiguredTextRepresentation() throws Exception {
		
		BooleanConverter standardConverter = new BooleanConverter();
		Assert.assertEquals(standardConverter.convert(Boolean.TRUE), "true");
		Assert.assertEquals(standardConverter.convert(Boolean.FALSE), "false");
		Assert.assertEquals(standardConverter.convert(null), "");
		
		BooleanConverter customConverter = new BooleanConverter("oui", "non", "?");
		Assert.assertEquals(customConverter.convert(Boolean.TRUE), "oui");
		Assert.assertEquals(customConverter.convert(Boolean.FALSE), "non");
		Assert.assertEquals(customConverter.convert(null), "?");
	}
}