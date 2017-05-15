package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.ConversionException;

public class EarliestCreatedConverterTest {
    /**
     * @verifies throw conversion exception when class cast fails
     * @see EarliestCreatedConverter#convert(Object)
     */
    @Test(expected = ConversionException.class)
    public void convert_shouldThrowConversionExceptionWhenClassCastFails() throws Exception {
        EarliestCreatedConverter converter = new EarliestCreatedConverter(null);
        converter.convert("invalid input");
    }
}
