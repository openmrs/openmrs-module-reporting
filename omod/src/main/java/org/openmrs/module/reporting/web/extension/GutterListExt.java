/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.extension;

import org.openmrs.module.web.extension.LinkExt;


public class GutterListExt extends LinkExt {

	public String getLabel() {
		return "reporting.title";
	}

	public String getUrl() {
		return "module/reporting/dashboard/index.form";
	}

	/**
	 * Returns the required privilege in order to see this section. Can be a
	 * comma delimited list of privileges. If the default empty string is
	 * returned, only an authenticated user is required
	 * 
	 * @return Privilege string
	 */
	public String getRequiredPrivilege() {
		return "View Reports";
	}

}
