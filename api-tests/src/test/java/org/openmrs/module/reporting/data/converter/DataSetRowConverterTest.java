package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.ConversionException;

public class DataSetRowConverterTest {
    /**
     * @verifies throw conversion exception when class cast fails
     * @see DataSetRowConverter#convert(Object)
     */
    @Test(expected = ConversionException.class)
    public void convert_shouldThrowConversionExceptionWhenClassCastFails() throws Exception {
        DataSetRowConverter converter = new DataSetRowConverter();
        converter.convert("invalid input");
    }
}
