package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.ConversionException;

public class ObsValueConverterTest {
    /**
     * @verifies throw conversion exception when class cast fails
     * @see ObsValueConverter#convert(Object)
     */
    @Test(expected = ConversionException.class)
    public void convert_shouldThrowConversionExceptionWhenClassCastFails() throws Exception {
        ObsValueConverter converter = new ObsValueConverter();
        converter.convert("invalid test input");
    }
}
