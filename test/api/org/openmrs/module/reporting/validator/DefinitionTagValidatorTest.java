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
package org.openmrs.module.reporting.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.DuplicateTagException;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.DefinitionTag;
import org.openmrs.module.reporting.definition.service.DefinitionServiceTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public class DefinitionTagValidatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String INITIAL_TEST_DATA_XML = "org/openmrs/module/reporting/include/ReportTestDataset.xml";
	
	protected static final String TAG_1 = "one";
	
	protected static final String GENDER_DEFINITION_UUID = "9023095e-7f5a-11e1-8393-00248140a5eb";
	
	private CohortDefinitionService cohortService;
	
	@Before
	public void before() throws Exception {
		executeDataSet(INITIAL_TEST_DATA_XML);
		cohortService = Context.getService(CohortDefinitionService.class);
	}
	
	/**
	 * @see {@link DefinitionTagValidator#validate(Object,Errors)}
	 */
	@Test(expected = DuplicateTagException.class)
	@Verifies(value = "should fail if the definition already has the tag", method = "validate(Object,Errors)")
	public void validate_shouldFailIfTheDefinitionAlreadyHasTheTag() throws Exception {
		GenderCohortDefinition genderDefinition = DefinitionServiceTest.getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		Assert.assertTrue(cohortService.hasTag(genderDefinition, TAG_1));
		DefinitionTag definitionTag = new DefinitionTag(TAG_1, genderDefinition);
		ValidateUtil.validate(definitionTag);
	}
	
	/**
	 * @see {@link DefinitionTagValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail if the definition tag is null", method = "validate(Object,Errors)")
	public void validate_shouldFailIfTheDefinitionTagIsNull() throws Exception {
		GenderCohortDefinition genderDefinition = DefinitionServiceTest.getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		DefinitionTag definitionTag = new DefinitionTag(null, genderDefinition);
		
		Errors errors = new BindException(definitionTag, "definitionTag");
		new DefinitionTagValidator().validate(definitionTag, errors);
		Assert.assertTrue(errors.hasFieldErrors("tag"));
	}
	
	/**
	 * @see {@link DefinitionTagValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail if the definition type is null", method = "validate(Object,Errors)")
	public void validate_shouldFailIfTheDefinitionTypeIsNull() throws Exception {
		GenderCohortDefinition genderDefinition = DefinitionServiceTest.getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		DefinitionTag definitionTag = new DefinitionTag("new", genderDefinition);
		definitionTag.setDefinitionType(null);
		Errors errors = new BindException(definitionTag, "definitionTag");
		new DefinitionTagValidator().validate(definitionTag, errors);
		Assert.assertTrue(errors.hasFieldErrors("definitionType"));
	}
	
	/**
	 * @see {@link DefinitionTagValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail if the definition uuid is null", method = "validate(Object,Errors)")
	public void validate_shouldFailIfTheDefinitionUuidIsNull() throws Exception {
		GenderCohortDefinition genderDefinition = DefinitionServiceTest.getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		DefinitionTag definitionTag = new DefinitionTag("new", genderDefinition);
		definitionTag.setDefinitionUuid(null);
		
		Errors errors = new BindException(definitionTag, "definitionTag");
		new DefinitionTagValidator().validate(definitionTag, errors);
		Assert.assertTrue(errors.hasFieldErrors("definitionUuid"));
	}
	
	/**
	 * @see {@link DefinitionTagValidator#validate(Object,Errors)}
	 */
	@Test(expected = IllegalArgumentException.class)
	@Verifies(value = "should fail if the object is null", method = "validate(Object,Errors)")
	public void validate_shouldFailIfTheObjectIsNull() throws Exception {
		Errors errors = new BindException(new DefinitionTag(), "definitionTag");
		new DefinitionTagValidator().validate(null, errors);
	}
	
	/**
	 * @see {@link DefinitionTagValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		GenderCohortDefinition genderDefinition = DefinitionServiceTest.getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		DefinitionTag definitionTag = new DefinitionTag("new", genderDefinition);
		definitionTag.setDefinitionType(GenderCohortDefinition.class.getName());
		definitionTag.setDefinitionUuid("uuid");
		
		Errors errors = new BindException(definitionTag, "definitionTag");
		new DefinitionTagValidator().validate(definitionTag, errors);
		Assert.assertFalse(errors.hasErrors());
	}
	
	/**
	 * @see {@link DefinitionTagValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail if tag or type or definition uuid has zero length", method = "validate(Object,Errors)")
	public void validate_shouldFailIfTagOrTypeOrDefinitionUuidHasZeroLength() throws Exception {
		GenderCohortDefinition genderDefinition = DefinitionServiceTest.getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		DefinitionTag definitionTag = new DefinitionTag("new", genderDefinition);
		definitionTag.setDefinitionUuid("");
		
		Errors errors = new BindException(definitionTag, "definitionTag");
		new DefinitionTagValidator().validate(definitionTag, errors);
		Assert.assertTrue(errors.hasFieldErrors("definitionUuid"));
	}
	
	/**
	 * @see {@link DefinitionTagValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail if tag or type or definition uuid is a white space character", method = "validate(Object,Errors)")
	public void validate_shouldFailIfTagOrTypeOrDefinitionUuidIsAWhiteSpaceCharacter() throws Exception {
		GenderCohortDefinition genderDefinition = DefinitionServiceTest.getDefinition(GenderCohortDefinition.class,
		    GENDER_DEFINITION_UUID);
		DefinitionTag definitionTag = new DefinitionTag(" ", genderDefinition);
		
		Errors errors = new BindException(definitionTag, "definitionTag");
		new DefinitionTagValidator().validate(definitionTag, errors);
		Assert.assertTrue(errors.hasFieldErrors("tag"));
	}
}
