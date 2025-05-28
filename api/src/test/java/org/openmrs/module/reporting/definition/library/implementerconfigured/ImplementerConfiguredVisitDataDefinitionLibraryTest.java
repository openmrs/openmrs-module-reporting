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
import org.openmrs.module.reporting.data.visit.definition.SqlVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitIdDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ImplementerConfiguredVisitDataDefinitionLibraryTest extends BaseImplementerConfiguredLibraryTest {

	@Autowired
	ImplementerConfiguredVisitDataDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		copyResource("visitData", "patientIdSql.sql");
		copyResource("visitData", "visitIdXml.reportingserializerxml");
		library.setDirectory(getConfigDir("visitData"));
		library.loadDefinitions();
	}

	@Test
	public void testSqlDefinition() throws Exception {
		VisitDataDefinition definition = library.getDefinition("configuration.definitionlibrary.visitData.patientIdSql");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(SqlVisitDataDefinition.class));
		assertThat(((SqlVisitDataDefinition) definition).getSql().trim(), is("select visit_id, patient_id from visit"));
	}

	@Test
	public void testSerializedDefinition() throws Exception {
		VisitDataDefinition definition = library.getDefinition("configuration.definitionlibrary.visitData.visitIdXml");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(VisitIdDataDefinition.class));
	}
}
