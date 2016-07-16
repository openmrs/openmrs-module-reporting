package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.ConversionException;

/**
 * Created by suny on 16.07.16.
 */
public class ChangeInValueConverterTest {
    /**
     * @verifies throw conversion exception when class cast fails
     * @see ChangeInValueConverter#convert(Object)
     */
    @Test(expected = ConversionException.class)
    public void convert_shouldThrowConversionExceptionWhenClassCastFails() throws Exception {
        ChangeInValueConverter converter = new ChangeInValueConverter();
        converter.convert("invalid input");
    }
}
