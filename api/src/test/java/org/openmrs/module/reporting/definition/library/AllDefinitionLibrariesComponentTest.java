package org.openmrs.module.reporting.definition.library;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class AllDefinitionLibrariesComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    AllDefinitionLibraries libraries;

    @Test
    public void testSetup() throws Exception {
        assertThat(libraries.getLibraries().size(), is(2));
    }

}
