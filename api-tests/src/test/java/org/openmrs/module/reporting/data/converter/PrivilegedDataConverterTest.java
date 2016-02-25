package org.openmrs.module.reporting.data.converter;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PrivilegedDataConverterTest extends BaseModuleContextSensitiveTest {

    public static final String INPUT = "input";
    public static final String REPLACEMENT = "****";

    @Test
    public void testConvertWithPrivilege() throws Exception {
        PrivilegedDataConverter converter = new PrivilegedDataConverter("A privilege I have");
        converter.setReplacement(REPLACEMENT);
        assertThat((String) converter.convert(INPUT), is(INPUT));
    }

    @Test
    @DirtiesContext
    public void testConvertWithoutPrivilege() throws Exception {
        Context.becomeUser("butch");

        PrivilegedDataConverter converter = new PrivilegedDataConverter("A privilege I do not have");
        converter.setReplacement(REPLACEMENT);
        assertThat((String) converter.convert(INPUT), is(REPLACEMENT));
    }

}