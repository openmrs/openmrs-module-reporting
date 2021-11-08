/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.test;

import org.apache.commons.lang3.StringUtils;
import org.junit.AssumptionViolatedException;
import org.openmrs.module.ModuleUtil;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class OpenmrsVersionTestListener extends AbstractTestExecutionListener {
	
	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		Class<?> testClass = testContext.getTestClass();
		
		RequiresVersion requiresVersionAnnotation = testClass.getAnnotation(RequiresVersion.class);
		
		if (requiresVersionAnnotation == null || StringUtils.isBlank(requiresVersionAnnotation.value())) {
			return;
		}
		
		if (!ModuleUtil.matchRequiredVersions(OpenmrsConstants.OPENMRS_VERSION,
				requiresVersionAnnotation.value())) {
			throw new AssumptionViolatedException(
					OpenmrsConstants.OPENMRS_VERSION + " does not match " + requiresVersionAnnotation.value());
		}
	}
}
