package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;

public class ConverterHelperTest {
    /**
     * @verifies convert object to target class
     * @see ConverterHelper#convertTo(Object, Class)
     */
    @Test
    public void convertTo_shouldConvertObjectToTargetClass() throws Exception {
        Object input = new Integer(42);
        Integer result = ConverterHelper.convertTo(input, Integer.class);
    }

    /**
     * @verifies handle null as input
     * @see ConverterHelper#convertTo(Object, Class)
     */
    @Test(expected=IllegalArgumentException.class)
    public void convertTo_shouldHandleNullAsInput() throws Exception {
        ConverterHelper.convertTo(null,null);
    }

    /**
     * @verifies handle null as input for orginal
     * @see ConverterHelper#convertTo(Object, Class)
     */
    @Test
    public void convertTo_shouldHandleNullAsInputForOrginal() throws Exception {
        ConverterHelper.convertTo(null,Integer.class);
    }
}
