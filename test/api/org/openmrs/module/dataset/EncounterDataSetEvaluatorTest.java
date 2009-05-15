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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.DataSetService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.util.LogicCriteriaBuilder;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * 
 */
@SkipBaseSetup
public class EncounterDataSetEvaluatorTest extends BaseContextSensitiveTest {
	
	/** Logger */
	private static Log log = LogFactory.getLog(EncounterDataSetEvaluatorTest.class);
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	@Override
    public Boolean useInMemoryDatabase() { return false; }	
	 */
	
	/**
	 * Runs the basic stuff since we have SkipBaseSetup on the whole class
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		//executeDataSet("org/openmrs/include/standardTestDatabase.xml");
		executeDataSet("org/openmrs/api/include/EncounterServiceTest-initialData.xml");
		authenticate();
		
	}

	
	@Test
	public void shouldEvaluateDataExportDataSet() throws Exception {
		EvaluationContext evalContext = new EvaluationContext();
		CohortDefinition cohortDefinition = 
			Context.getService(CohortDefinitionService.class).getAllPatientsCohortDefinition();
		
		DataSetDefinition dataSetDefinition = 
			new EncounterDataSetDefinition();
		
		// 
		Cohort baseCohort = 
			Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evalContext);
		
		evalContext.setBaseCohort(baseCohort);
		
		DataSet dataSet = 
			Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, evalContext);
	
		for (DataSetColumn column : dataSet.getDataSetDefinition().getColumns()) { 
			log.info("Column: " + column.getKey() + " " + column.getDataType());			
		}

		for (Object row : dataSet) { 
			HashMap<DataSetColumn, Object> columnSet = (HashMap<DataSetColumn, Object>) row;			
			for (DataSetColumn column : columnSet.keySet()) { 
				Object value = columnSet.get(column);
				log.info(column.getColumnName() + " = " + value);
			}			
		}
		
	}
	
}
