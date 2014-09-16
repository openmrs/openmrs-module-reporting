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
package org.openmrs.module.reporting.indicator.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.test.Verifies;

/**
 * Testing the utility methods within PeriodIndicatorReportUtil
 */
public class IndicatorUtilTest {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
     * @see {@link PeriodIndicatorReportUtil#compileColumnDimensionOptions(Map)}
     */
    @Test
    @Verifies(value = "return all combinations of dimension options", method = "compileColumnDimensionOptions(Map)")
	public void compileColumnDimensionOptions_shouldReturnAllCombinationsOfDimensionOptions() { 
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		
		CohortDefinitionDimension ages = new CohortDefinitionDimension();
		ages.addCohortDefinition("Adult", null);
		ages.addCohortDefinition("Child", null);
		ages.addCohortDefinition("UnknownAge", null);
		dsd.addDimension("Age", new Mapped<CohortDefinitionDimension>(ages, null));
		
		CohortDefinitionDimension genders = new CohortDefinitionDimension();
		genders.addCohortDefinition("Male", null);
		genders.addCohortDefinition("Female", null);
		dsd.addDimension("Gender", new Mapped<CohortDefinitionDimension>(genders, null));
		
		CohortDefinitionDimension locations = new CohortDefinitionDimension();
		locations.addCohortDefinition("Boston", null);
		locations.addCohortDefinition("Indianapolis", null);
		locations.addCohortDefinition("Rwinkwavu", null);
		locations.addCohortDefinition("Eldoret", null);
		dsd.addDimension("Location", new Mapped<CohortDefinitionDimension>(locations, null));
		
		{
			Map<String, List<String>> toInclude = new LinkedHashMap<String, List<String>>();
			toInclude.put("Age", Arrays.asList("Adult", "Child", "Unknown"));
			toInclude.put("Gender", Arrays.asList("Male", "Female"));
			toInclude.put("Location", Arrays.asList("Boston", "Indianapolis", "Rwinkwavu", "Eldoret"));
	
			Set<String> options = new HashSet<String>(IndicatorUtil.compileColumnDimensionOptions(toInclude));
			Assert.assertEquals(59, options.size());
		}

		{
			Map<String, List<String>> toInclude = new LinkedHashMap<String, List<String>>();
			toInclude.put("Age", Arrays.asList("Adult", "Child"));
			toInclude.put("Gender", Arrays.asList("Male", "Female"));
			toInclude.put("Location", Arrays.asList("Boston", "Rwinkwavu"));
	
			Set<String> options = new HashSet<String>(IndicatorUtil.compileColumnDimensionOptions(toInclude));
			Assert.assertEquals(26, options.size());
		}
		
		{
			Map<String, List<String>> toInclude = new LinkedHashMap<String, List<String>>();
			toInclude.put("Age", Arrays.asList("Adult", "Child"));
			toInclude.put("Gender", Arrays.asList("Male", "Female"));
	
			Set<String> options = new HashSet<String>(IndicatorUtil.compileColumnDimensionOptions(toInclude));
			Assert.assertEquals(8, options.size());
		}
	}
}