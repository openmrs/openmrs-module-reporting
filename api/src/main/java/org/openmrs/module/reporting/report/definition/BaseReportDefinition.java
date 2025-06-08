/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.definition;

import org.openmrs.module.reporting.common.Localized;

/**
 * This class exists to allow for classes to extend that would otherwise need to extend
 * ReportDefinition.  This will enable us to more easily turn ReportDefinition into an interface
 * in the future
 */
@Localized("reporting.ReportDefinition")
public class BaseReportDefinition extends ReportDefinition {
	
	public static final long serialVersionUID = 1L;

	//***********************
	// CONSTRUCTORS
	//***********************
	
	public BaseReportDefinition() {
		super();
	}
}
