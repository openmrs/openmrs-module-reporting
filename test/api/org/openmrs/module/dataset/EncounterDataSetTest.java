/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.dataset;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.util.LogicCriteriaBuilder;
import org.openmrs.module.cohort.definition.PatientCharacteristicCohortDefinition;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test class that tries to run a portion of the
 */
public class EncounterDataSetTest extends BaseModuleContextSensitiveTest {
	
	private static Log log = LogFactory.getLog(EncounterDataSetTest.class);
	
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
    public Boolean useInMemoryDatabase() {
	    return false;
	}





	/**
	 * Auto generated method comment
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldEvaludateDataSet() throws Exception {

		//initializeInMemoryDatabase();
		//executeDataSet("org/openmrs/report/include/ReportTests-patients.xml");
		
		
		authenticate();
		
		
		EvaluationContext evalContext = new EvaluationContext();
		PatientCharacteristicCohortDefinition kids = new PatientCharacteristicCohortDefinition();
		kids.setName("Cohort");
		kids.setMaxAge(10);
		
		
		DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);		
		
		EncounterDataSetDefinition definition = new EncounterDataSetDefinition();
		definition.setFilter(kids);

		
		
		DataSet<Object> data = service.evaluate(definition, evalContext);
		for (Map<DataSetColumn, Object> row : data) {
			for (Map.Entry<DataSetColumn, Object> e : row.entrySet()) {
				log.info(e.getKey() + " -> " + e.getValue());
			}
			
		}

	}
}
