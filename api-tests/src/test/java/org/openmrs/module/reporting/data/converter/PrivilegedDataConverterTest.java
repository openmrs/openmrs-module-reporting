/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

@SkipBaseSetup
public class PrivilegedDataConverterTest extends BaseModuleContextSensitiveTest {

    public static final String INPUT = "input";
    public static final String REPLACEMENT = "****";
    
    public static final String HAS_PRIV = "A privilege I have";
    public static final String DOES_NOT_HAVE_PRIV = "A privilege I do not have";
    
    @BeforeEach
    public void setUp() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("org/openmrs/module/reporting/include/PrivilegeTest.xml");
    }
    
    @Test
    public void testConvertWithPrivilege() {
        try {
            Context.addProxyPrivilege(HAS_PRIV);
            Context.hasPrivilege(HAS_PRIV);
            PrivilegedDataConverter converter = new PrivilegedDataConverter(HAS_PRIV);
            converter.setReplacement(REPLACEMENT);
            assertThat((String) converter.convert(INPUT), is(INPUT));
        }
        finally {
            Context.removeProxyPrivilege(HAS_PRIV);
        }
    }

    @Test
    public void testConvertWithoutPrivilege() throws Exception {
        PrivilegedDataConverter converter = new PrivilegedDataConverter(DOES_NOT_HAVE_PRIV);
        converter.setReplacement(REPLACEMENT);
        assertThat((String) converter.convert(INPUT), is(REPLACEMENT));
    }

}