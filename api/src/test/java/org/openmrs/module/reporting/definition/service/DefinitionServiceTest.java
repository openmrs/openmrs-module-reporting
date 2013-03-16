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
