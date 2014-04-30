package org.openmrs.module.reporting.data.converter;

import org.junit.Test;

public class DataConverterTest {

    @Test (expected = ConversionException.class)
    public void convert_shouldThrowConversionExceptionWhenUnableToConvertObject() {
        getDataConverter().convert(new Object());
    }

    private DataConverter getDataConverter() {
        return new DataConverter() {
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
