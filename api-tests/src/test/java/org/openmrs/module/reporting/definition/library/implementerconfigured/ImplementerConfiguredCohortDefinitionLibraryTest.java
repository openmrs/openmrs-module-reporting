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
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EvaluatableCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ImplementerConfiguredCohortDefinitionLibraryTest extends BaseImplementerConfiguredLibraryTest {

	@Autowired
	ImplementerConfiguredCohortDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		copyResource("cohort", "femalesSql.sql");
		copyResource("cohort", "femalesXml.reportingserializerxml");
		copyResource("cohort", "femalesGroovy.groovy");
		library.setDirectory(getConfigDir("cohort"));
		library.loadDefinitions();
	}

	@Test
	public void testSqlDefinition() throws Exception {
		CohortDefinition definition = library.getDefinition("configuration.definitionlibrary.cohort.femalesSql");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(SqlCohortDefinition.class));
		assertThat(((SqlCohortDefinition) definition).getQuery().trim(), is("select person_id from person where gender = 'F'"));
	}

	@Test
	public void testSerializedDefinition() throws Exception {
		CohortDefinition definition = library.getDefinition("configuration.definitionlibrary.cohort.femalesXml");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(GenderCohortDefinition.class));
		assertThat(((GenderCohortDefinition) definition).getFemaleIncluded(), is(true));
	}

	@Test
	public void testGroovyDefinition() throws Exception {
		CohortDefinition definition = library.getDefinition("configuration.definitionlibrary.cohort.femalesGroovy");
		assertThat(definition, notNullValue());
		assertThat(definition, instanceOf(EvaluatableCohortDefinition.class));
	}
}
