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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class ImplementerConfiguredDataSetDefinitionLibraryComponentTest extends BaseModuleContextSensitiveTest {

	@Autowired
	DataSetDefinitionService service;

	@Autowired
	ImplementerConfiguredDataSetDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		File dir = new File(getClass().getClassLoader().getResource("implementerconfigured/dataSet.groovy").toURI()
				.getPath()).getParentFile();
		library.setDirectory(dir);
	}

	@Test
	public void testGroovyWithAutowired() throws Exception {
		DataSetDefinition definition = library.getDefinition("configuration.definitionlibrary.dataset.dataSet");
		DataSet dataSet = service.evaluate(definition, new EvaluationContext());
		assertThat(dataSet.getMetaData().getColumnCount(), is(1));
		assertThat(dataSet.iterator().next().getColumnValue("groovy"), Is.<Object>is("Xanadu"));
	}
}
