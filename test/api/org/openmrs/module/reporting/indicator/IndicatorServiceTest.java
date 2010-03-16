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
package org.openmrs.module.reporting.indicator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Testing the cohort definition persister.  
 */
public class IndicatorServiceTest extends BaseModuleContextSensitiveTest {

	// Logger
	protected Log log = LogFactory.getLog(this.getClass());	
	
	@Before
	public void runBeforeTest() throws Exception { 
		authenticate();		
	}
	
	@Test
	public void shouldSaveIndicator() throws Exception { 		
		IndicatorService service = Context.getService(IndicatorService.class);
		CohortIndicator cohortIndicator = new CohortIndicator();		
		cohortIndicator.setName("Testing");
		Indicator savedIndicator = service.saveIndicator(cohortIndicator);		
		Assert.assertTrue(savedIndicator.getId()!=null);
	}
	



}