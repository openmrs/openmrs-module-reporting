/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.library.implementerconfigured;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.SqlEncounterDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ImplementerConfiguredEncounterDataDefinitionLibraryTest extends BaseImplementerConfiguredLibraryTest {

	@Autowired
	ImplementerConfiguredEncounterDataDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		copyResource("encounterData", "patientIdSql.sql");
		copyResource("encounterData", "encounterDatetimeXml.reportingserializerxml");
		library.setDirectory(getConfigDir("encounterData"));
		library.loadDefinitions();
	}

	@Test
	public void testSqlDefinition() throws Exception {
		EncounterDataDefinition definition = library.getDefinition("configuration.definitionlibrary.encounterData.patientIdSql");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(SqlEncounterDataDefinition.class));
		assertThat(((SqlEncounterDataDefinition) definition).getSql().trim(), is("select encounter_id, patient_id from encounter"));
	}

	@Test
	public void testSerializedDefinition() throws Exception {
		EncounterDataDefinition definition = library.getDefinition("configuration.definitionlibrary.encounterData.encounterDatetimeXml");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(EncounterDatetimeDataDefinition.class));
	}

}
