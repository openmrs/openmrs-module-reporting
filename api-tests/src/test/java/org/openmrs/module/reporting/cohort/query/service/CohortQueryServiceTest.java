/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.query.service;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CohortQueryServiceTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	@Verifies(value = "should get patients having encounters with a specified provider", method = "getPatientsHavingEncounters(Date, Date, TimeQualifier, List<Location>, List<Person>, List<EncounterType>, List<Form>, Integer, Integer, User, Date, Date)")
	public void getPatientsHavingEncounters_shouldGetPatientsHavingEncountersWithASpecifiedProvider() throws Exception {
		List<Person> providerList = Collections.singletonList(new Person(2));
		CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingEncounters(null, null, TimeQualifier.ANY, null, providerList, null, null, null, null, null, null, null);
		assertCohort(cohort, 23, 24);
	}


	private void assertCohort(Cohort cohort, Integer... memberIds) {
	    Assert.assertEquals("Cohort was supposed to be: " + Arrays.asList(memberIds) + " but was instead: " + cohort.getCommaSeparatedPatientIds(), memberIds.length, cohort.size());
	    for (Integer memberId : memberIds)
	    	Assert.assertTrue("Cohort does not contain patient " + memberId, cohort.contains(memberId));
    }

}