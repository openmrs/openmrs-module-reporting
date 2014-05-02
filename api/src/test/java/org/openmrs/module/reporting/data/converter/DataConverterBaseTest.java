package org.openmrs.module.reporting.data.converter;

import org.junit.Test;

public class DataConverterBaseTest {

    @Test (expected = ConversionException.class)
    public void convert_shouldThrowConversionExceptionWhenUnableToConvertObject() {
        getDataConverter().convert(new Object());
    }

    private DataConverterBase getDataConverter() {
        return new DataConverterBase() {
            @Override
            protected Object convertObject(Object original) {
                throw new RuntimeException();
            }

            @Override
            public Class<?> getInputDataType() {
                return null;
            }

            @Override
            public Class<?> getDataType() {
                return null;
            }
        };
    }
}
