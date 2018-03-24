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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.reporting.data.visit.definition.SqlVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitIdDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenmrsUtil.class)
public class ImplementerConfiguredDataSetDefinitionLibraryTest {

	public static final String SQL_QUERY = "select patient_id from patient";

	private File directory;

	private ImplementerConfiguredDataSetDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		directory = mock(File.class);
		when(directory.exists()).thenReturn(true);
		when(directory.isDirectory()).thenReturn(true);

		library = new ImplementerConfiguredDataSetDefinitionLibrary();
		library.setDirectory(directory);
	}

	@Test
	public void testSqlDefinition() throws Exception {
		when(directory.listFiles()).thenReturn(new File[] { new File("patientId.sql") });

		mockStatic(OpenmrsUtil.class);
		when(OpenmrsUtil.getFileAsString(any(File.class))).thenReturn(SQL_QUERY);

		assertThat(library.getDefinitionSummaries().size(), is(1));
		assertThat(library.getDefinitionSummaries().get(0).getKey(), is("configuration.definitionlibrary.dataset.patientId"));

		DataSetDefinition definition = library.getDefinition("configuration.definitionlibrary.dataset.patientId");
		assertThat(definition, instanceOf(SqlDataSetDefinition.class));
		assertThat(((SqlDataSetDefinition) definition).getSqlQuery(), is(SQL_QUERY));
	}

	@Test
	public void testSerializedDefinition() throws Exception {
		when(directory.listFiles()).thenReturn(new File[] { new File("patientId.reportingserializerxml") });

		mockStatic(OpenmrsUtil.class);
		when(OpenmrsUtil.getFileAsString(any(File.class))).
				thenReturn("<org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition>" +
						"<parameters/>" +
						"<sqlQuery>select patient_id from patient</sqlQuery>" +
						"</org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition>");

		assertThat(library.getDefinitionSummaries().size(), is(1));
		assertThat(library.getDefinitionSummaries().get(0).getKey(), is("configuration.definitionlibrary.dataset.patientId"));

		DataSetDefinition definition = library.getDefinition("configuration.definitionlibrary.dataset.patientId");
		assertThat(definition, instanceOf(DataSetDefinition.class));
	}

}
