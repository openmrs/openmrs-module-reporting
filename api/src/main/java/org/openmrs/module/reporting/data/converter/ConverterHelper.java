package org.openmrs.module.reporting.data.converter;

import org.openmrs.calculation.ConversionException;

public class ConverterHelper {
    /**
     * @should convert object to target class
     * @should handle null as input
     * @should handle null as input for orginal
     */

    public static <T> T convertTo(Object original, Class<T> targetClass) {
        if (targetClass == null) {
            throw new IllegalArgumentException("targetClass must not be null");
        }
        try {
            T convertedObject = targetClass.cast(original);
            return convertedObject;
        } catch (ClassCastException cce) {
            throw new ConversionException(String.format("casting '%s' into %s failed", original, targetClass.getSimpleName()));
        }
    }
}
