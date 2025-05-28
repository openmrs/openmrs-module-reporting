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
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ImplementerConfiguredDataSetDefinitionLibraryTest extends BaseImplementerConfiguredLibraryTest {

	@Autowired
	ImplementerConfiguredDataSetDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		copyResource("dataset", "patientIdSql.sql");
		copyResource("dataset", "patientIdXml.reportingserializerxml");
		copyResource("dataset", "testGroovy.groovy");
		library.setDirectory(getConfigDir("dataset"));
		library.loadDefinitions();
	}

	@Test
	public void testSqlDefinition() throws Exception {
		DataSetDefinition definition = library.getDefinition("configuration.definitionlibrary.dataset.patientIdSql");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(SqlDataSetDefinition.class));
		assertThat(((SqlDataSetDefinition) definition).getSqlQuery().trim(), is("select patient_id from patient"));
	}

	@Test
	public void testSerializedDefinition() throws Exception {
		DataSetDefinition definition = library.getDefinition("configuration.definitionlibrary.dataset.patientIdXml");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(SqlDataSetDefinition.class));
		assertThat(((SqlDataSetDefinition) definition).getSqlQuery().trim(), is("select patient_id from patient"));
	}

	@Test
	public void testGroovyDefinition() throws Exception {
		DataSetDefinition definition = library.getDefinition("configuration.definitionlibrary.dataset.testGroovy");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(EvaluatableDataSetDefinition.class));
	}
}
