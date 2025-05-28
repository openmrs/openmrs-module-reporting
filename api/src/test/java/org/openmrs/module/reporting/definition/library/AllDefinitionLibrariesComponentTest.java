/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.library;

import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.service.PatientDataServiceImplTest;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.library.BuiltInVisitDataLibrary;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.library.implementerconfigured.ImplementerConfiguredCohortDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.implementerconfigured.ImplementerConfiguredDataSetDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.implementerconfigured
        .ImplementerConfiguredEncounterDataDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.implementerconfigured
        .ImplementerConfiguredPatientDataDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.implementerconfigured.ImplementerConfiguredVisitDataDefinitionLibrary;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.instanceOf;
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
        assertThat(libraries.getLibraries(), containsInAnyOrder(
                instanceOf(BuiltInCohortDefinitionLibrary.class),
                instanceOf(BuiltInPatientDataLibrary.class),
                instanceOf(BuiltInEncounterDataLibrary.class),
                instanceOf(BuiltInVisitDataLibrary.class),
                instanceOf(ImplementerConfiguredCohortDefinitionLibrary.class),
                instanceOf(ImplementerConfiguredDataSetDefinitionLibrary.class),
                instanceOf(ImplementerConfiguredPatientDataDefinitionLibrary.class),
                instanceOf(ImplementerConfiguredVisitDataDefinitionLibrary.class),
                instanceOf(ImplementerConfiguredEncounterDataDefinitionLibrary.class),
                instanceOf(BaseDefinitionLibraryTest.TestDefinitionLibrary.class)
        ));
    }

    @Test
    public void testGetAllDefinitionTypes() throws Exception {
        assertThat(libraries.getAllDefinitionTypes(), containsInAnyOrder(
                CohortDefinition.class,
                PatientDataDefinition.class,
                EncounterDataDefinition.class,
                VisitDataDefinition.class,
                DataSetDefinition.class));
    }

}
