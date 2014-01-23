/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.reporting.report.service.db;

import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PropertiesTypeTest {

    @Test
    public void testThatWritingAPropertyWithSlashesPreservesThem() throws Exception {
        String expectedProperty = "blacklistRegex";
        String expectedValue = "[^\\p{InBasicLatin}\\p{InLatin1Supplement}]";
        Properties properties = new Properties();
        properties.setProperty(expectedProperty, expectedValue);

        PropertiesType propertiesType = new PropertiesType();
        String serialized = (String) propertiesType.disassemble(properties);
        Properties deserialized = (Properties) propertiesType.assemble(serialized, null);

        assertThat(deserialized.size(), is(1));
        assertThat(deserialized.getProperty(expectedProperty), is(expectedValue));
    }

}
