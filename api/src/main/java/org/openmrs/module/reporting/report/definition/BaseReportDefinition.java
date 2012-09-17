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
