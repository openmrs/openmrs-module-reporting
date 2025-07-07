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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class PrivilegedDataConverterTest {

    public static final String INPUT = "input";
    public static final String REPLACEMENT = "****";
    
    public static final String HAS_PRIV = "A privilege I have";
    public static final String DOES_NOT_HAVE_PRIV = "A privilege I do not have";
    
    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Context.class);
        PowerMockito.when(Context.hasPrivilege(HAS_PRIV)).thenReturn(true);
        PowerMockito.when(Context.hasPrivilege(DOES_NOT_HAVE_PRIV)).thenReturn(false);
    }
    
    @Test
    public void testConvertWithPrivilege() throws Exception {
        PrivilegedDataConverter converter = new PrivilegedDataConverter(HAS_PRIV);
        converter.setReplacement(REPLACEMENT);
        assertThat((String) converter.convert(INPUT), is(INPUT));
    }

    @Test
    public void testConvertWithoutPrivilege() throws Exception {
        PrivilegedDataConverter converter = new PrivilegedDataConverter(DOES_NOT_HAVE_PRIV);
        converter.setReplacement(REPLACEMENT);
        assertThat((String) converter.convert(INPUT), is(REPLACEMENT));
    }

}