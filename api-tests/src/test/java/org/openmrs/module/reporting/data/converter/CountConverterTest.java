package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.ConversionException;

public class CountConverterTest {
    /**
     * @verifies throw conversion exception when class cast fails
     * @see CountConverter#convert(Object)
     */
    @Test(expected = ConversionException.class)
    public void convert_shouldThrowConversionExceptionWhenClassCastFails() throws Exception {
        CountConverter converter = new CountConverter();
        converter.convert("invalid input");
    }
}
