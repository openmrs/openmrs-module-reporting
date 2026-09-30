/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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