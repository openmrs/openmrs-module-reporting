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
package org.openmrs.module.reporting.data.person.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.RelationshipsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

public class RelationshipsForPersonDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link org.openmrs.test.BaseContextSensitiveTest} is run right before this method.
	 *
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void evaluate_shouldReturnAllRelationships() throws Exception {

		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,7"));

		RelationshipsForPersonDataDefinition d = new RelationshipsForPersonDataDefinition();
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);

		Assert.assertEquals(2, pd.getData().size());
		Assert.assertEquals(1, getRelationships(pd, 2).size());
		Assert.assertEquals(3, getRelationships(pd, 7).size());

		testHasRelationship(pd, 2, getDoctorPatientType(), 502);
		testHasRelationship(pd, 7, getDoctorPatientType(), 502);
		testHasRelationship(pd, 7, getParentChildType(), 23);
		testHasRelationship(pd, 7, getParentChildType(), 24);
	}

	@Test
	public void evaluate_shouldReturnRelationshipsOfType() throws Exception {

		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7"));

		RelationshipsForPersonDataDefinition d = new RelationshipsForPersonDataDefinition();
		d.setRelationshipTypes(Arrays.asList(getDoctorPatientType()));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);

		Assert.assertEquals(1, pd.getData().size());
		Assert.assertEquals(1, getRelationships(pd, 7).size());

		d.setRelationshipTypes(Arrays.asList(getParentChildType()));
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(1, pd.getData().size());
		Assert.assertEquals(2, getRelationships(pd, 7).size());
	}

	@Test
	public void evaluate_shouldReturnRelationshipsByAorB() throws Exception {

		EvaluationContext context = new EvaluationContext();

		RelationshipsForPersonDataDefinition d = new RelationshipsForPersonDataDefinition();

		d.setValuesArePersonA(Boolean.FALSE);
		d.setValuesArePersonB(Boolean.TRUE);
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertNull(getRelationships(pd, 7));

		d.setValuesArePersonA(Boolean.TRUE);
		d.setValuesArePersonB(Boolean.FALSE);
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(3, getRelationships(pd, 7).size());
	}

	protected RelationshipType getDoctorPatientType() {
		return Context.getPersonService().getRelationshipType(1);
	}

	protected RelationshipType getParentChildType() {
		return Context.getPersonService().getRelationshipType(5);
	}

	protected List<Relationship> getRelationships(EvaluatedPersonData pd, Integer pId) {
		return (List<Relationship>)pd.getData().get(pId);
	}

	protected void testHasRelationship(EvaluatedPersonData pd, Integer pId, RelationshipType type, Integer relationId) {
		List<Relationship> l = getRelationships(pd, pId);
		boolean found = false;
		if (l != null) {
			for (Relationship r : l) {
				if (r.getRelationshipType().equals(type)) {
					Person p = (r.getPersonA().getPersonId().equals(pId) ? r.getPersonB() : r.getPersonA());
					found = found || p.getPersonId().equals(relationId);
				}
			}
		}
		Assert.assertTrue("Not able to find " + relationId + " as a " + type.getaIsToB() + " or " + type.getbIsToA() + " of " + pId, found);
	}
}
