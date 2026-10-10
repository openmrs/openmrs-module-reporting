/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation.parameter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

public class ParameterizableUtilTest {

	@Test
	public void getMappedType_shouldReturnTheGenericTypeOfAMappedMapProperty() {
		Assertions.assertEquals(DataSetDefinition.class,
		    ParameterizableUtil.getMappedType(ReportDefinition.class, "dataSetDefinitions"));
	}

	@Test
	public void getMappedType_shouldRejectAPropertyTheTypeDoesNotHave() {
		IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
		    () -> ParameterizableUtil.getMappedType(ReportDefinition.class, "noSuchProperty"));
		Assertions.assertEquals("Cannot retrieve Mapped type from: ReportDefinition.noSuchProperty", e.getMessage());
		Assertions.assertNull(e.getCause(), "a missing property must be reported, not surface as " + e.getCause());
	}

	@Test
	public void getMappedType_shouldRejectANullType() {
		IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
		    () -> ParameterizableUtil.getMappedType(null, "dataSetDefinitions"));
		Assertions.assertEquals("Cannot retrieve Mapped type from: null.dataSetDefinitions", e.getMessage());
	}
}
