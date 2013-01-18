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
package org.openmrs.module.reporting.cohort.definition.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.Verifies;

public class CohortExpressionParserTest {
	
	/**
	 * @see {@link CohortExpressionParser#parseIntoTokens(String)}
	 */
	@Test
	@Verifies(value = "should parse an expression containing multiple allowed characters", method = "parseIntoTokens(String)")
	public void parseIntoTokens_shouldParseAnExpressionContainingMultipleAllowedCharacters() throws Exception {
		List<Object> tokens = CohortExpressionParser.parseIntoTokens("all the_allowed_characters");
		Assert.assertEquals(2, tokens.size());
		Assert.assertTrue(tokens.contains("the_allowed_characters"));
	}
}
