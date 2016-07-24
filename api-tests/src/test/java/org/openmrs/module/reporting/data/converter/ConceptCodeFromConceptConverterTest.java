package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.ConversionException;

public class ConceptCodeFromConceptConverterTest {
    /**
     * @verifies throw conversion exception when class cast fails
     * @see ConceptCodeFromConceptConverter#convert(Object)
     */
    @Test(expected = ConversionException.class)
    public void convert_shouldThrowConversionExceptionWhenClassCastFails() throws Exception {
        ConceptCodeFromConceptConverter converter = new ConceptCodeFromConceptConverter("sth");
        converter.convert("invalid test input");
    }
}
