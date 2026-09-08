/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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