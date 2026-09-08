/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.service;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class DefinitionServiceTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	DataSetDefinitionService dataSetDefinitionService;
	
	/**
	 * This test passes on JDK 1.6, but fails on JDK 1.7. See REPORT-468.
	 * 
	 * In JDK 1.7 inner classes must be static to be properly deserialized by xstream.
	 * 
	 * @see DefinitionService#getDefinitionByUuid(String)
	 * @verifies deserialize CohortIndicatorAndDimensionDataSetDefinition
	 */
	@Test
	public void getDefinitionByUuid_shouldDeserializeCohortIndicatorAndDimensionDataSetDefinition() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/DefinitionServiceTest.xml");
		
		CohortIndicatorAndDimensionDataSetDefinition persistedDefinition = (CohortIndicatorAndDimensionDataSetDefinition) dataSetDefinitionService
		        .getDefinitionByUuid("bb1dc014-82a0-4847-8bcd-f74f91282e8d");
		assertThat(persistedDefinition, notNullValue());
		assertThat(persistedDefinition.getName(), is("Patients in 2006 by indicators"));
		assertThat(persistedDefinition.getSpecifications(), not(empty()));
	}
}
