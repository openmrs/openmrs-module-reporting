package org.openmrs.module.reporting.data.converter;


import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.ConversionException;
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

	/**
	 * @verifies throw conversion exception when class cast fails
	 * @see BooleanConverter#convert(Object)
	 */
	@Test(expected = ConversionException.class)
	public void convert_shouldThrowConversionExceptionWhenClassCastFails() throws Exception {
	    BooleanConverter converter = new BooleanConverter();
		converter.convert("inalid input");
	}
}