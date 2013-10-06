package org.openmrs.module.reporting.data.converter;

import java.util.Collection;

/**
 * Returns the count of elements in a collection
 */
public class CountConverter implements DataConverter {

    private boolean returnNullInsteadOfZero = false;

    public CountConverter() {
    }

    public CountConverter(boolean returnNullInsteadOfZero) {
        this.returnNullInsteadOfZero = returnNullInsteadOfZero;
    }

    @Override
    public Object convert(Object original) {
        Collection c = (Collection) original;
        int size = c == null ? 0 : c.size();
        return (returnNullInsteadOfZero && size == 0) ? null : size;
    }

    @Override
    public Class<?> getInputDataType() {
        return Collection.class;
    }

    @Override
    public Class<?> getDataType() {
        return Integer.class;
    }
}
