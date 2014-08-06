package org.openmrs.module.reporting.definition.library;

import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
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
        assertThat(libraries.getLibraries().size(), is(5)); // One of these is a Test library
    }

    @Test
    public void testGetAllDefinitionTypes() throws Exception {
        assertThat(libraries.getAllDefinitionTypes(), containsInAnyOrder(CohortDefinition.class, PatientDataDefinition.class, EncounterDataDefinition.class, VisitDataDefinition.class));
    }

}
