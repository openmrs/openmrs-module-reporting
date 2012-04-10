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
package org.openmrs.module.reporting.definition.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.record.formula.functions.T;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Contains tests for the {@link DefinitionService}
 */
public class DefinitionServiceTest extends BaseModuleContextSensitiveTest {
	
	protected static final String INITIAL_TEST_DATA_XML = "org/openmrs/module/reporting/include/ReportTestDataset.xml";
	
	protected static final String TAG_1 = "one";
	
	protected static final String TAG_2 = "two";
	
	protected static final String TAG_3 = "three";
	
	protected static final String GENDER_DEFINITION_UUID = "9023095e-7f5a-11e1-8393-00248140a5eb";
	
	private CohortDefinitionService cohortService;
	
	@Before
	public void before() throws Exception {
		executeDataSet(INITIAL_TEST_DATA_XML);
		cohortService = Context.getService(CohortDefinitionService.class);
	}
	
	/**
	 * @see {@link DefinitionService#addTagToDefinition(T,String)}
	 */
	@Test
	@Verifies(value = "should add the specified tag to the specified definition", method = "addTagToDefinition(T,String)")
	public void addTagToDefinition_shouldAddTheSpecifiedTagToTheSpecifiedDefinition() throws Exception {
		GenderCohortDefinition genderDefinition = getDefinition(GenderCohortDefinition.class, GENDER_DEFINITION_UUID);
		//sanity check that the definition doesn't have the tag yet
		Assert.assertTrue(!cohortService.hasTag(genderDefinition, TAG_2));
		Assert.assertTrue(cohortService.addTagToDefinition(genderDefinition, TAG_2));
		
		//the definition should have been added and saved
		Assert.assertTrue(cohortService.hasTag(genderDefinition, TAG_2));
	}
	
	/**
	 * @see {@link DefinitionService#getAllDefinitionsByTag()}
	 */
	@Test
	@Verifies(value = "should get all definitions as summaries of the service type grouped by tags", method = "getAllDefinitionsByTag()")
	public void getAllDefinitionsByTag_shouldGetAllDefinitionsAsSummariesOfTheServiceTypeGroupedByTags() throws Exception {
		Map<String, List<DefinitionSummary>> tagDefsMap = cohortService.getAllDefinitionsByTag();
		Assert.assertEquals(3, tagDefsMap.size());
		Assert.assertEquals(2, tagDefsMap.get(TAG_1).size());
		Assert.assertEquals(1, tagDefsMap.get(TAG_2).size());
		Assert.assertEquals(1, tagDefsMap.get(TAG_3).size());
	}
	
	/**
	 * @see {@link DefinitionService#getAllDefinitionsHavingTag(String)}
	 */
	@Test
	@Verifies(value = "should get all definition as summaries with the specified tag", method = "getAllDefinitionsHavingTag(String)")
	public void getAllDefinitionsHavingTag_shouldGetAllDefinitionAsSummariesWithTheSpecifiedTag() throws Exception {
		Assert.assertEquals(2, cohortService.getAllDefinitionsHavingTag(TAG_1).size());
	}
	
	/**
	 * @see {@link DefinitionService#getAllDefinitionTags()}
	 */
	@Test
	@Verifies(value = "should get all definition tags applied to any definition of the service type", method = "getAllDefinitionTags()")
	public void getAllDefinitionTags_shouldGetAllDefinitionTagsAppliedToAnyDefinitionOfTheServiceType() throws Exception {
		Assert.assertEquals(4, cohortService.getAllDefinitionTags().size());
	}
	
	/**
	 * @see {@link DefinitionService#getAllTags()}
	 */
	@Test
	@Verifies(value = "should get all tags applied to any definition of the service type", method = "getAllTags()")
	public void getAllTags_shouldGetAllTagsAppliedToAnyDefinitionOfTheServiceType() throws Exception {
		Assert.assertEquals(3, cohortService.getAllTags().size());
	}
	
	/**
	 * @see {@link DefinitionService#removeTagFromDefinition(T,String)}
	 */
	@Test
	@Verifies(value = "should remove the specified tag from the specified definition", method = "removeTagFromDefinition(T,String)")
	public void removeTagFromDefinition_shouldRemoveTheSpecifiedTagFromTheSpecifiedDefinition() throws Exception {
		CohortDefinition genderDefinition = (CohortDefinition) getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		
		//sanity check that the definition actually has the tag
		Assert.assertTrue(cohortService.hasTag(genderDefinition, TAG_3));
		
		cohortService.removeTagFromDefinition(genderDefinition, TAG_3);
		
		//the definition should have been removed
		Assert.assertTrue(!cohortService.hasTag(genderDefinition, TAG_3));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(T,String)}
	 */
	@Test
	@Verifies(value = "should return false if the definition doesnt have the tag", method = "hasTag(T,String)")
	public void hasTag_shouldReturnFalseIfTheDefinitionDoesntHaveTheTag() throws Exception {
		CohortDefinition genderDefinition = (CohortDefinition) getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		Assert.assertFalse(cohortService.hasTag(genderDefinition, TAG_2));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(T,String)}
	 */
	@Test
	@Verifies(value = "should return false if the definition is null", method = "hasTag(T,String)")
	public void hasTag_shouldReturnFalseIfTheDefinitionIsNull() throws Exception {
		CohortDefinition cd = null;
		Assert.assertFalse(cohortService.hasTag(cd, TAG_3));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(T,String)}
	 */
	@Test
	@Verifies(value = "should return false if the tag is null", method = "hasTag(T,String)")
	public void hasTag_shouldReturnFalseIfTheTagIsNull() throws Exception {
		CohortDefinition genderDefinition = (CohortDefinition) getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		Assert.assertFalse(cohortService.hasTag(genderDefinition, null));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(T,String)}
	 */
	@Test
	@Verifies(value = "should return true if the definition has the tag", method = "hasTag(T,String)")
	public void hasTag_shouldReturnTrueIfTheDefinitionHasTheTag() throws Exception {
		CohortDefinition genderDefinition = (CohortDefinition) getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		Assert.assertTrue(cohortService.hasTag(genderDefinition, TAG_1));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(T,String)}
	 */
	@Test
	@Verifies(value = "should return false if the definition has a null uuid", method = "hasTag(T,String)")
	public void hasTag_shouldReturnFalseIfTheDefinitionHasANullUuid() throws Exception {
		CohortDefinition genderDefinition = (CohortDefinition) getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		genderDefinition.setUuid(null);
		Assert.assertFalse(cohortService.hasTag(genderDefinition, TAG_2));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(String,String)}
	 */
	@Test
	@Verifies(value = "should return false if the matching definition doesnt have the tag", method = "hasTag(String,String)")
	public void hasTag_shouldReturnFalseIfTheMatchingDefinitionDoesntHaveTheTag() throws Exception {
		Assert.assertFalse(cohortService.hasTag(GENDER_DEFINITION_UUID, TAG_2));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(String,String)}
	 */
	@Test
	@Verifies(value = "should return false if the tag parameter is null", method = "hasTag(String,String)")
	public void hasTag_shouldReturnFalseIfTheTagParameterIsNull() throws Exception {
		Assert.assertFalse(cohortService.hasTag(GENDER_DEFINITION_UUID, null));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(String,String)}
	 */
	@Test
	@Verifies(value = "should return false if the uuid is null", method = "hasTag(String,String)")
	public void hasTag_shouldReturnFalseIfTheUuidIsNull() throws Exception {
		Assert.assertFalse(cohortService.hasTag((String) null, TAG_2));
	}
	
	/**
	 * @see {@link DefinitionService#hasTag(String,String)}
	 */
	@Test
	@Verifies(value = "should return true if the matching definition has the tag", method = "hasTag(String,String)")
	public void hasTag_shouldReturnTrueIfTheMatchingDefinitionHasTheTag() throws Exception {
		Assert.assertTrue(cohortService.hasTag(GENDER_DEFINITION_UUID, TAG_1));
	}
	
	/**
	 * Utility class that creates a definition of the specified type with the specified uuid
	 * 
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <D extends Definition> D getDefinition(Class<D> clazz, String uuid) throws InstantiationException,
	    IllegalAccessException {
		D definition = clazz.newInstance();
		definition.setUuid(uuid);
		return definition;
	}
	
	/**
	 * @see {@link DefinitionService#getTags(T)}
	 */
	@Test
	@Verifies(value = "should get all the tags applied to the definition", method = "getTags(T)")
	public void getTags_shouldGetAllTheTagsAppliedToTheDefinition() throws Exception {
		CohortDefinition genderDefinition = (CohortDefinition) getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		List<String> tags = cohortService.getTags(genderDefinition);
		Assert.assertEquals(2, tags.size());
		Assert.assertTrue(tags.contains(TAG_1));
		Assert.assertTrue(tags.contains(TAG_3));
	}
}
