package org.openmrs.module.reporting.data.converter;

import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PrivilegedDataFormatterTest extends BaseModuleContextSensitiveTest {

    public static final String INPUT = "input";
    public static final String REPLACEMENT = "****";

    @Test
    public void testConvertWithPrivilege() throws Exception {
        PrivilegedDataFormatter formatter = new PrivilegedDataFormatter("A privilege I have");
        formatter.setReplacement(REPLACEMENT);

        assertThat((String) formatter.convert(INPUT), is(INPUT));
    }

    @Test
    @DirtiesContext
    public void testConvertWithoutPrivilege() throws Exception {
        List<User> allUsers = Context.getUserService().getAllUsers();
        Context.becomeUser("butch");
        PrivilegedDataFormatter formatter = new PrivilegedDataFormatter("A privilege I do not have");
        formatter.setReplacement(REPLACEMENT);

        assertThat((String) formatter.convert("input"), is(REPLACEMENT));
    }

}