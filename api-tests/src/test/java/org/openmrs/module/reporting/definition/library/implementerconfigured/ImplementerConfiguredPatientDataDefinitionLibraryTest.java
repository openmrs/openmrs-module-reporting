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
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ImplementerConfiguredPatientDataDefinitionLibraryTest extends BaseImplementerConfiguredLibraryTest {

	@Autowired
	ImplementerConfiguredPatientDataDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		copyResource("patientData", "patientIdSql.sql");
		copyResource("patientData", "patientIdXml.reportingserializerxml");
		library.setDirectory(getConfigDir("patientData"));
		library.loadDefinitions();
	}

	@Test
	public void testSqlDefinition() throws Exception {
		PatientDataDefinition definition = library.getDefinition("configuration.definitionlibrary.patientData.patientIdSql");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(SqlPatientDataDefinition.class));
		assertThat(((SqlPatientDataDefinition) definition).getSql().trim(), is("select patient_id, patient_id from patient"));
	}

	@Test
	public void testSerializedDefinition() throws Exception {
		PatientDataDefinition definition = library.getDefinition("configuration.definitionlibrary.patientData.patientIdXml");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(PatientIdDataDefinition.class));
	}

}
